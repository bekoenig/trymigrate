package io.github.bekoenig.trymigrate.core.internal.jupiter.extension;

import io.github.bekoenig.trymigrate.core.Trymigrate;
import io.github.bekoenig.trymigrate.core.plugin.TrymigrateBeanProvider;
import io.github.bekoenig.trymigrate.core.plugin.customize.TrymigrateContainerConfigurer;
import io.github.bekoenig.trymigrate.core.plugin.customize.TrymigrateFlywayConfigurer;
import io.github.bekoenig.trymigrate.core.internal.bean.BeanProviderFactory;
import io.github.bekoenig.trymigrate.core.internal.bean.PluginDiscovery;
import io.github.bekoenig.trymigrate.core.internal.bean.PluginProvider;
import io.github.bekoenig.trymigrate.core.internal.flyway.FlywayConfigurationFactory;
import io.github.bekoenig.trymigrate.core.internal.flyway.callback.SchemaLinter;
import io.github.bekoenig.trymigrate.core.internal.jupiter.StoreSupport;
import io.github.bekoenig.trymigrate.core.internal.schemacrawler.catalog.CatalogFactory;
import io.github.bekoenig.trymigrate.core.internal.schemacrawler.lint.LintPattern;
import io.github.bekoenig.trymigrate.core.internal.schemacrawler.lint.LintPatterns;
import io.github.bekoenig.trymigrate.core.internal.schemacrawler.lint.LintsAssert;
import io.github.bekoenig.trymigrate.core.internal.schemacrawler.lint.LintsHistory;
import io.github.bekoenig.trymigrate.core.internal.schemacrawler.lint.config.CompositeLinterRegistry;
import io.github.bekoenig.trymigrate.core.lint.IgnoreLint;
import io.github.bekoenig.trymigrate.core.lint.config.LintersCustomizer;
import io.github.bekoenig.trymigrate.core.lint.report.LintsReporter;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.MigrationVersion;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestInstancePostProcessor;
import org.junit.platform.commons.support.AnnotationSupport;
import org.testcontainers.containers.JdbcDatabaseContainer;
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
        AnnotatedElement annotatedElement = extensionContext.getElement().orElseThrow();
        Trymigrate testConfiguration = AnnotationSupport.findAnnotation(annotatedElement,
                Trymigrate.class).orElseThrow();

        List<PluginProvider> pluginProviders = new PluginDiscovery().discover(testConfiguration.plugin());
        TrymigrateBeanProvider beanProvider = new BeanProviderFactory().create(o, pluginProviders);
        StoreSupport.putBeanProvider(extensionContext, beanProvider);

        beanProvider.findOne(JdbcDatabaseContainer.class).ifPresent(jdbcDatabaseContainer ->
                beanProvider.all(TrymigrateContainerConfigurer.class)
                        .forEach(customizer -> customizer.accept(jdbcDatabaseContainer)));

        MigrationVersion initialVersion = MigrationVersion.EMPTY;
        StoreSupport.putMigrationVersion(extensionContext, initialVersion);

        LintsHistory lintsHistory = new LintsHistory(ignoredLintsFromAnnotation(annotatedElement));
        lintsHistory.putLints(initialVersion.getVersion(), new Lints(List.of()));
        StoreSupport.putLintsHistory(extensionContext, lintsHistory);

        SchemaLinter schemaLinter = new SchemaLinter(
                compositeLinterRegistry(beanProvider),
                compositeLintersCustomizer(beanProvider),
                new CatalogFactory(beanProvider.first(LimitOptions.class), beanProvider.first(LoadOptions.class)),
                catalog -> StoreSupport.putCatalog(extensionContext, catalog),
                lintsHistory, beanProvider.all(LintsReporter.class));
        FlywayConfigurationFactory flywayConfigurationFactory = new FlywayConfigurationFactory(
                testConfiguration.flywayProperties(),
                configuration -> beanProvider.all(TrymigrateFlywayConfigurer.class)
                        .forEach(configurer -> configurer.accept(configuration)),
                schemaLinter);
        StoreSupport.putFlywayConfigurationFactory(extensionContext, flywayConfigurationFactory);

        Flyway flyway = flywayConfigurationFactory.get().load();
        flyway.info();
        StoreSupport.putDataSource(extensionContext, flyway.getConfiguration().getDataSource());

        StoreSupport.putLintsAssert(extensionContext, new LintsAssert(testConfiguration.failOn()));
    }

    private LintPatterns ignoredLintsFromAnnotation(AnnotatedElement annotatedElement) {
        return new LintPatterns(AnnotationSupport.findRepeatableAnnotations(
                        annotatedElement, IgnoreLint.class).stream()
                .map(x -> new LintPattern(x.linterId(), x.objectName())).toList());
    }

    private LintersCustomizer compositeLintersCustomizer(TrymigrateBeanProvider beanProvider) {
        return lintersBuilder -> beanProvider.all(LintersCustomizer.class)
                .forEach(x -> x.accept(lintersBuilder));
    }

    private LinterInitializer compositeLinterRegistry(TrymigrateBeanProvider beanProvider) {
        CompositeLinterRegistry compositeLinterRegistry = new CompositeLinterRegistry();
        beanProvider.all(LinterProvider.class).forEach(compositeLinterRegistry::register);
        return compositeLinterRegistry;
    }

}
