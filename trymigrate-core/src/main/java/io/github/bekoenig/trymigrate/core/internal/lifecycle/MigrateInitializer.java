package io.github.bekoenig.trymigrate.core.internal.lifecycle;

import io.github.bekoenig.trymigrate.core.Trymigrate;
import io.github.bekoenig.trymigrate.core.internal.StoreSupport;
import io.github.bekoenig.trymigrate.core.internal.catalog.CatalogFactory;
import io.github.bekoenig.trymigrate.core.internal.lint.*;
import io.github.bekoenig.trymigrate.core.internal.lint.config.CompositeLinterRegistry;
import io.github.bekoenig.trymigrate.core.internal.migrate.MigrateProcessor;
import io.github.bekoenig.trymigrate.core.internal.plugin.BeanProviderFactory;
import io.github.bekoenig.trymigrate.core.internal.plugin.PluginDiscovery;
import io.github.bekoenig.trymigrate.core.lint.TrymigrateExcludeLint;
import io.github.bekoenig.trymigrate.core.lint.config.TrymigrateLintersConfigurer;
import io.github.bekoenig.trymigrate.core.lint.report.TrymigrateLintsReporter;
import io.github.bekoenig.trymigrate.core.plugin.TrymigrateBeanProvider;
import io.github.bekoenig.trymigrate.core.plugin.customize.TrymigrateDataLoader;
import io.github.bekoenig.trymigrate.core.plugin.customize.TrymigrateFlywayCustomizer;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestInstancePostProcessor;
import org.junit.platform.commons.support.AnnotationSupport;
import org.testcontainers.containers.JdbcDatabaseContainer;
import schemacrawler.schemacrawler.LimitOptions;
import schemacrawler.schemacrawler.LoadOptions;
import schemacrawler.tools.lint.LinterProvider;

import java.lang.reflect.AnnotatedElement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MigrateInitializer implements TestInstancePostProcessor {

    @Override
    public void postProcessTestInstance(Object o, ExtensionContext extensionContext) {
        Trymigrate testConfiguration = AnnotationSupport.findAnnotation(o.getClass(),
                Trymigrate.class).orElseThrow();

        TrymigrateBeanProvider beanProvider = new BeanProviderFactory().create(o, new PluginDiscovery().discover(
                testConfiguration.discoverPlugin(), testConfiguration.excludePlugins()));

        CatalogFactory catalogFactory = new CatalogFactory(
                beanProvider.first(LimitOptions.class), beanProvider.first(LoadOptions.class));

        LintProcessor lintProcessor = new LintProcessor(
                new CompositeLinterRegistry(beanProvider.all(LinterProvider.class)),
                allLintersConfigurers(beanProvider),
                new LintsHistory(excludedLintPatterns(o.getClass())), beanProvider.all(TrymigrateLintsReporter.class),
                new LintsAssert(testConfiguration.failOn()));

        MigrateProcessor migrateProcessor = new MigrateProcessor(
                beanProvider.findOne(JdbcDatabaseContainer.class).orElse(null),
                splitProperties(testConfiguration.flywayProperties()),
                beanProvider.all(TrymigrateFlywayCustomizer.class),
                beanProvider.all(TrymigrateDataLoader.class),
                catalogFactory,
                lintProcessor);
        StoreSupport.putMigrateProcessor(extensionContext, migrateProcessor);

        migrateProcessor.prepare();
    }

    private List<TrymigrateLintersConfigurer> allLintersConfigurers(TrymigrateBeanProvider beanProvider) {
        List<TrymigrateLintersConfigurer> lintersConfigurers = new ArrayList<>(
                beanProvider.all(TrymigrateLintersConfigurer.class));
        // configurers with high order are applied at last to manipulate base definitions
        Collections.reverse(lintersConfigurers);
        return lintersConfigurers;
    }

    private LintPatterns excludedLintPatterns(AnnotatedElement annotatedElement) {
        return new LintPatterns(AnnotationSupport.findRepeatableAnnotations(
                        annotatedElement, TrymigrateExcludeLint.class).stream()
                .map(x -> new LintPattern(x.linterId(), x.objectName())).toList());
    }

    private Map<String, String> splitProperties(String[] properties) {
        return Stream.of(properties)
                .map(property -> {
                    String[] tokens = property.split("=");
                    if (tokens.length != 2) {
                        throw new IllegalArgumentException("Property '%s' does not match format 'key=value'"
                                .formatted(property));
                    }
                    return tokens;
                })
                .collect(Collectors.toMap(
                        split -> normalizePrefix(split[0]),
                        split -> split[1]));
    }

    private String normalizePrefix(String propertyName) {
        if (propertyName.startsWith("flyway.")) {
            return propertyName;
        }
        return "flyway." + propertyName;
    }

}
