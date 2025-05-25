package io.github.bekoenig.trymigrate.core.internal.lifecycle;

import io.github.bekoenig.trymigrate.core.Trymigrate;
import io.github.bekoenig.trymigrate.core.internal.container.StaticPortBinding;
import io.github.bekoenig.trymigrate.core.plugin.TrymigrateBeanProvider;
import io.github.bekoenig.trymigrate.core.plugin.customize.TrymigrateFlywayCustomizer;
import io.github.bekoenig.trymigrate.core.internal.plugin.BeanProviderFactory;
import io.github.bekoenig.trymigrate.core.internal.plugin.PluginDiscovery;
import io.github.bekoenig.trymigrate.core.internal.plugin.PluginProvider;
import io.github.bekoenig.trymigrate.core.internal.migrate.FlywayConfigurationFactory;
import io.github.bekoenig.trymigrate.core.internal.migrate.callback.SchemaLinter;
import io.github.bekoenig.trymigrate.core.internal.StoreSupport;
import io.github.bekoenig.trymigrate.core.internal.catalog.CatalogFactory;
import io.github.bekoenig.trymigrate.core.internal.lint.LintPattern;
import io.github.bekoenig.trymigrate.core.internal.lint.LintPatterns;
import io.github.bekoenig.trymigrate.core.internal.lint.LintsAssert;
import io.github.bekoenig.trymigrate.core.internal.lint.LintsHistory;
import io.github.bekoenig.trymigrate.core.internal.lint.config.CompositeLinterRegistry;
import io.github.bekoenig.trymigrate.core.lint.TrymigrateExcludeLint;
import io.github.bekoenig.trymigrate.core.lint.config.TrymigrateLintersCustomizer;
import io.github.bekoenig.trymigrate.core.lint.report.TrymigrateLintsReporter;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.MigrationVersion;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestInstancePostProcessor;
import org.junit.platform.commons.support.AnnotationSupport;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.lifecycle.Startable;
import schemacrawler.schemacrawler.LimitOptions;
import schemacrawler.schemacrawler.LoadOptions;
import schemacrawler.tools.lint.LinterInitializer;
import schemacrawler.tools.lint.LinterProvider;
import schemacrawler.tools.lint.Lints;

import java.lang.reflect.AnnotatedElement;
import java.util.List;

public class MigrateInitializer implements TestInstancePostProcessor {

    @Override
    public void postProcessTestInstance(Object o, ExtensionContext extensionContext) {
        Trymigrate testConfiguration = AnnotationSupport.findAnnotation(o.getClass(),
                Trymigrate.class).orElseThrow();

        List<PluginProvider> pluginProviders = new PluginDiscovery().discover(testConfiguration.plugin());
        TrymigrateBeanProvider beanProvider = new BeanProviderFactory().create(o, pluginProviders);
        StoreSupport.putBeanProvider(extensionContext, beanProvider);

        beanProvider.findOne(JdbcDatabaseContainer.class).ifPresent(jdbcDatabaseContainer ->
                new StaticPortBinding().andThen(Startable::start).accept(jdbcDatabaseContainer));

        MigrationVersion initialVersion = MigrationVersion.EMPTY;
        StoreSupport.putMigrationVersion(extensionContext, initialVersion);

        LintsHistory lintsHistory = new LintsHistory(excludedLintPatternsFromAnnotation(o.getClass()));
        lintsHistory.put(initialVersion, new Lints(List.of()));
        StoreSupport.putLintsHistory(extensionContext, lintsHistory);

        SchemaLinter schemaLinter = new SchemaLinter(
                compositeLinterRegistry(beanProvider),
                compositeLintersCustomizer(beanProvider),
                new CatalogFactory(beanProvider.first(LimitOptions.class), beanProvider.first(LoadOptions.class)),
                catalog -> StoreSupport.putCatalog(extensionContext, catalog),
                lintsHistory, beanProvider.all(TrymigrateLintsReporter.class));
        FlywayConfigurationFactory flywayConfigurationFactory = new FlywayConfigurationFactory(
                testConfiguration.flywayProperties(),
                configuration -> beanProvider.all(TrymigrateFlywayCustomizer.class)
                        .forEach(configurer -> configurer.accept(configuration)),
                schemaLinter);
        StoreSupport.putFlywayConfigurationFactory(extensionContext, flywayConfigurationFactory);

        Flyway flyway = flywayConfigurationFactory.get().load();
        flyway.info();
        StoreSupport.putDataSource(extensionContext, flyway.getConfiguration().getDataSource());

        StoreSupport.putLintsAssert(extensionContext, new LintsAssert(testConfiguration.failOn()));
    }

    private LintPatterns excludedLintPatternsFromAnnotation(AnnotatedElement annotatedElement) {
        return new LintPatterns(AnnotationSupport.findRepeatableAnnotations(
                        annotatedElement, TrymigrateExcludeLint.class).stream()
                .map(x -> new LintPattern(x.linterId(), x.objectName())).toList());
    }

    private TrymigrateLintersCustomizer compositeLintersCustomizer(TrymigrateBeanProvider beanProvider) {
        return lintersBuilder -> beanProvider.all(TrymigrateLintersCustomizer.class)
                .forEach(x -> x.accept(lintersBuilder));
    }

    private LinterInitializer compositeLinterRegistry(TrymigrateBeanProvider beanProvider) {
        CompositeLinterRegistry compositeLinterRegistry = new CompositeLinterRegistry();
        beanProvider.all(LinterProvider.class).forEach(compositeLinterRegistry::register);
        return compositeLinterRegistry;
    }

}
