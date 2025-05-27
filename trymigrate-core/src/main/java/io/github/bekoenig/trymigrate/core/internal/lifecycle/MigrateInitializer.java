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
import io.github.bekoenig.trymigrate.core.lint.config.TrymigrateLintersCustomizer;
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
import schemacrawler.tools.lint.LinterInitializer;
import schemacrawler.tools.lint.LinterProvider;

import java.lang.reflect.AnnotatedElement;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MigrateInitializer implements TestInstancePostProcessor {

    @Override
    public void postProcessTestInstance(Object o, ExtensionContext extensionContext) {
        Trymigrate testConfiguration = AnnotationSupport.findAnnotation(o.getClass(),
                Trymigrate.class).orElseThrow();

        TrymigrateBeanProvider beanProvider = new BeanProviderFactory().create(o,
                new PluginDiscovery().discover(testConfiguration.plugin()));

        CatalogFactory catalogFactory = new CatalogFactory(
                beanProvider.first(LimitOptions.class), beanProvider.first(LoadOptions.class));

        LintProcessor lintProcessor = new LintProcessor(createLinterInitializer(beanProvider),
                beanProvider.all(TrymigrateLintersCustomizer.class),
                beanProvider.all(TrymigrateLintsReporter.class),
                new LintsHistory(excludedLintPatternsFromAnnotation(o.getClass())),
                new LintsAssert(testConfiguration.failOn()));

        MigrateProcessor testProcessor = new MigrateProcessor(
                beanProvider.findOne(JdbcDatabaseContainer.class).orElse(null),
                splitProperties(testConfiguration.flywayProperties()),
                beanProvider.all(TrymigrateFlywayCustomizer.class),
                beanProvider.all(TrymigrateDataLoader.class),
                catalogFactory,
                lintProcessor);
        StoreSupport.putMigrateProcessor(extensionContext, testProcessor);

        testProcessor.prepare();
    }

    private LinterInitializer createLinterInitializer(TrymigrateBeanProvider beanProvider) {
        CompositeLinterRegistry compositeLinterRegistry = new CompositeLinterRegistry();
        beanProvider.all(LinterProvider.class).forEach(compositeLinterRegistry::register);
        return compositeLinterRegistry;
    }

    private LintPatterns excludedLintPatternsFromAnnotation(AnnotatedElement annotatedElement) {
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
