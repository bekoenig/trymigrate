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
import java.util.*;

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
                excludedLintPatterns(o.getClass()),
                new CompositeLinterRegistry(beanProvider.all(LinterProvider.class)),
                allLintersConfigurers(beanProvider),
                new LintsHistory(),
                beanProvider.all(TrymigrateLintsReporter.class),
                new LintsAssert(testConfiguration.failOn()));

        MigrateProcessor migrateProcessor = new MigrateProcessor(
                resolveJdbcDatabaseContainer(beanProvider),
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

    private Map<String, String> splitProperties(String[] keyValues) {
        Map<String, String> properties = new HashMap<>();
        for (String keyValue : keyValues) {
            int delimiterIndex = keyValue.indexOf("=");
            if (delimiterIndex < 0) {
                throw new IllegalArgumentException("Property '%s' does not match format 'key=value'"
                        .formatted(keyValue));
            }
            properties.put(normalizePrefix(keyValue.substring(0, delimiterIndex)),
                    keyValue.substring(delimiterIndex + 1));
        }
        return properties;
    }

    private String normalizePrefix(String propertyName) {
        if (propertyName.startsWith("flyway.")) {
            return propertyName;
        }
        return "flyway." + propertyName;
    }

    private JdbcDatabaseContainer<?> resolveJdbcDatabaseContainer(TrymigrateBeanProvider beanProvider) {
        try {
            return beanProvider.findOne(JdbcDatabaseContainer.class).orElse(null);
        } catch (NoClassDefFoundError e) {
            return null;
        }
    }

}
