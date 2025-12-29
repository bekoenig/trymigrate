package io.github.bekoenig.trymigrate.core.internal.lifecycle;

import io.github.bekoenig.trymigrate.core.internal.StoreSupport;
import io.github.bekoenig.trymigrate.core.internal.catalog.CatalogFactory;
import io.github.bekoenig.trymigrate.core.internal.lint.*;
import io.github.bekoenig.trymigrate.core.internal.lint.config.CompositeLinterRegistry;
import io.github.bekoenig.trymigrate.core.internal.migrate.MigrateProcessor;
import io.github.bekoenig.trymigrate.core.internal.plugin.BeanProviderFactory;
import io.github.bekoenig.trymigrate.core.internal.plugin.PluginDiscovery;
import io.github.bekoenig.trymigrate.core.lint.TrymigrateAssertLints;
import io.github.bekoenig.trymigrate.core.lint.TrymigrateExcludeLint;
import io.github.bekoenig.trymigrate.core.lint.config.TrymigrateLintersConfigurer;
import io.github.bekoenig.trymigrate.core.lint.report.TrymigrateLintOptionsCustomizer;
import io.github.bekoenig.trymigrate.core.lint.report.TrymigrateLintsReporter;
import io.github.bekoenig.trymigrate.core.plugin.TrymigrateBeanProvider;
import io.github.bekoenig.trymigrate.core.plugin.TrymigrateDiscoverPlugins;
import io.github.bekoenig.trymigrate.core.plugin.customize.TrymigrateDataLoader;
import io.github.bekoenig.trymigrate.core.plugin.customize.TrymigrateFlywayCustomizer;
import io.github.bekoenig.trymigrate.core.plugin.customize.TrymigrateCatalogCustomizer;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestInstancePostProcessor;
import org.junit.platform.commons.support.AnnotationSupport;
import org.testcontainers.containers.JdbcDatabaseContainer;
import schemacrawler.tools.command.lint.options.LintOptions;
import schemacrawler.tools.command.lint.options.LintOptionsBuilder;
import schemacrawler.tools.lint.LintSeverity;
import schemacrawler.tools.lint.LinterProvider;

import java.lang.reflect.AnnotatedElement;
import java.util.List;

public class MigrateInitializer implements TestInstancePostProcessor {

    @Override
    public void postProcessTestInstance(Object o, ExtensionContext extensionContext) {
        TrymigrateDiscoverPlugins discoverPlugins = AnnotationSupport.findAnnotation(o.getClass(),
                TrymigrateDiscoverPlugins.class).orElseThrow();

        TrymigrateBeanProvider beanProvider = new BeanProviderFactory().create(o, new PluginDiscovery().discover(
                discoverPlugins.origin(), discoverPlugins.exclude()));

        CatalogFactory catalogFactory = new CatalogFactory(
                beanProvider.allReservedOrder(TrymigrateCatalogCustomizer.class));

        LintSeverity failOn = AnnotationSupport.findAnnotation(o.getClass(),
                TrymigrateAssertLints.class).map(TrymigrateAssertLints::failOn).orElse(null);

        LintProcessor lintProcessor = new LintProcessor(
                excludedLintPatterns(o.getClass()),
                new CompositeLinterRegistry(beanProvider.all(LinterProvider.class)),
                beanProvider.allReservedOrder(TrymigrateLintersConfigurer.class),
                new LintsHistory(),
                beanProvider.all(TrymigrateLintsReporter.class),
                buildLintOptions(beanProvider.all(TrymigrateLintOptionsCustomizer.class)),
                new LintsAssert(failOn)
        );

        MigrateProcessor migrateProcessor = new MigrateProcessor(
                resolveJdbcDatabaseContainer(beanProvider),
                beanProvider.allReservedOrder(TrymigrateFlywayCustomizer.class),
                beanProvider.all(TrymigrateDataLoader.class),
                catalogFactory,
                lintProcessor);
        StoreSupport.putMigrateProcessor(extensionContext, migrateProcessor);

        migrateProcessor.prepare();
    }

    private LintPatterns excludedLintPatterns(AnnotatedElement annotatedElement) {
        return new LintPatterns(AnnotationSupport.findRepeatableAnnotations(
                        annotatedElement, TrymigrateExcludeLint.class).stream()
                .map(x -> new LintPattern(x.linterId(), x.objectName())).toList());
    }

    private LintOptions buildLintOptions(List<TrymigrateLintOptionsCustomizer> customizers) {
        LintOptionsBuilder lintOptionsBuilder = LintOptionsBuilder.builder().noInfo();
        customizers.forEach(x -> x.accept(lintOptionsBuilder));
        return lintOptionsBuilder.build();
    }

    private JdbcDatabaseContainer<?> resolveJdbcDatabaseContainer(TrymigrateBeanProvider beanProvider) {
        try {
            return beanProvider.findOne(JdbcDatabaseContainer.class).orElse(null);
        } catch (NoClassDefFoundError e) {
            return null;
        }
    }

}
