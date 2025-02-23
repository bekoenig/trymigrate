package io.github.bekoenig.trymigrate.core.internal.jupiter.extension;

import io.github.bekoenig.trymigrate.core.Trymigrate;
import io.github.bekoenig.trymigrate.core.config.TrymigrateBeanProvider;
import io.github.bekoenig.trymigrate.core.config.TrymigrateFlywayConfigurer;
import io.github.bekoenig.trymigrate.core.internal.bean.BeanProviderSupport;
import io.github.bekoenig.trymigrate.core.internal.flyway.FlywayConfigurationFactory;
import io.github.bekoenig.trymigrate.core.internal.flyway.callback.SchemaLinter;
import io.github.bekoenig.trymigrate.core.internal.jupiter.StoreSupport;
import io.github.bekoenig.trymigrate.core.internal.schemacrawler.lint.LintPattern;
import io.github.bekoenig.trymigrate.core.internal.schemacrawler.lint.LintsAssert;
import io.github.bekoenig.trymigrate.core.internal.schemacrawler.lint.LintsHistory;
import io.github.bekoenig.trymigrate.core.internal.schemacrawler.lint.LintPatterns;
import io.github.bekoenig.trymigrate.core.lint.IgnoreLint;
import io.github.bekoenig.trymigrate.core.lint.config.LintersCustomizer;
import io.github.bekoenig.trymigrate.core.lint.report.LintsReporter;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.MigrationVersion;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestInstancePostProcessor;
import org.junit.platform.commons.support.AnnotationSupport;
import org.testcontainers.containers.JdbcDatabaseContainer;
import schemacrawler.tools.lint.Lints;

import java.lang.reflect.AnnotatedElement;
import java.util.List;

public class MigrateInitializer implements TestInstancePostProcessor {

    @Override
    public void postProcessTestInstance(Object o, ExtensionContext extensionContext) {
        AnnotatedElement annotatedElement = extensionContext.getElement().orElseThrow();
        Trymigrate testConfiguration = AnnotationSupport.findAnnotation(annotatedElement,
                Trymigrate.class).orElseThrow();

        TrymigrateBeanProvider beanProvider = BeanProviderSupport.createHierarchy(o, testConfiguration.plugin());
        StoreSupport.putBeanProvider(extensionContext, beanProvider);

        beanProvider.findOne(JdbcDatabaseContainer.class).ifPresent(JdbcDatabaseContainer::start);

        MigrationVersion initialVersion = MigrationVersion.EMPTY;
        StoreSupport.putMigrationVersion(extensionContext, initialVersion);

        LintsHistory lintsHistory = new LintsHistory(ignoredLintsFromAnnotation(annotatedElement));
        lintsHistory.putLints(initialVersion.getVersion(), new Lints(List.of()));
        StoreSupport.putLintsHistory(extensionContext, lintsHistory);

        SchemaLinter schemaLinter = new SchemaLinter(beanProvider.all(LintersCustomizer.class),
                catalog -> StoreSupport.putCatalog(extensionContext, catalog),
                lintsHistory, beanProvider.all(LintsReporter.class));
        FlywayConfigurationFactory flywayConfigurationFactory = new FlywayConfigurationFactory(
                testConfiguration.flywayProperties(), () -> beanProvider.all(TrymigrateFlywayConfigurer.class), schemaLinter);
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

}
