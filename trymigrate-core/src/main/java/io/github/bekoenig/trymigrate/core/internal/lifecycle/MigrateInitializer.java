package io.github.bekoenig.trymigrate.core.internal.lifecycle;

import io.github.bekoenig.trymigrate.core.internal.StoreSupport;
import io.github.bekoenig.trymigrate.core.internal.catalog.CatalogFactory;
import io.github.bekoenig.trymigrate.core.internal.lint.*;
import io.github.bekoenig.trymigrate.core.internal.migrate.MigrateProcessor;
import io.github.bekoenig.trymigrate.core.internal.plugin.PluginDiscovery;
import io.github.bekoenig.trymigrate.core.internal.plugin.PluginRegistry;
import io.github.bekoenig.trymigrate.core.internal.plugin.PluginRegistryFactory;
import io.github.bekoenig.trymigrate.core.lint.TrymigrateAssertLints;
import io.github.bekoenig.trymigrate.core.lint.TrymigrateExcludeLint;
import io.github.bekoenig.trymigrate.core.plugin.TrymigrateDiscoverPlugins;
import io.github.bekoenig.trymigrate.core.plugin.customize.*;
import org.flywaydb.core.api.callback.Callback;
import org.flywaydb.core.api.migration.JavaMigration;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestInstancePostProcessor;
import org.junit.platform.commons.support.AnnotationSupport;
 import schemacrawler.tools.command.lint.options.LintOptions;
import schemacrawler.tools.command.lint.options.LintOptionsBuilder;
import schemacrawler.tools.lint.LintSeverity;

import java.lang.reflect.AnnotatedElement;
import java.util.List;

public class MigrateInitializer implements TestInstancePostProcessor {

    @Override
    public void postProcessTestInstance(Object o, ExtensionContext extensionContext) {
        TrymigrateDiscoverPlugins discoverPlugins = AnnotationSupport.findAnnotation(o.getClass(),
                TrymigrateDiscoverPlugins.class).orElseThrow();

        PluginRegistry pluginRegistry = new PluginRegistryFactory().create(o, new PluginDiscovery().discover(
                discoverPlugins.origin(), discoverPlugins.exclude()));

        CatalogFactory catalogFactory = new CatalogFactory(
                pluginRegistry.allReservedOrder(TrymigrateCatalogCustomizer.class));

        LintSeverity failOn = AnnotationSupport.findAnnotation(o.getClass(),
                TrymigrateAssertLints.class).map(TrymigrateAssertLints::failOn).orElse(null);

        LintProcessor lintProcessor = new LintProcessor(
                excludedLintPatterns(o.getClass()),
                pluginRegistry.allReservedOrder(TrymigrateLintersConfigurer.class),
                new LintsHistory(),
                pluginRegistry.all(TrymigrateLintsReporter.class),
                buildLintOptions(pluginRegistry.all(TrymigrateLintOptionsCustomizer.class)),
                new LintsAssert(failOn)
        );

        MigrateProcessor migrateProcessor = new MigrateProcessor(
                pluginRegistry.findOne(TrymigrateDatabase.class).orElse(null),
                pluginRegistry.allReservedOrder(TrymigrateFlywayCustomizer.class),
                pluginRegistry.all(Callback.class),
                pluginRegistry.all(JavaMigration.class),
                pluginRegistry.all(TrymigrateDataLoader.class),
                catalogFactory,
                lintProcessor);
        StoreSupport.putMigrateProcessor(extensionContext, migrateProcessor);
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

}
