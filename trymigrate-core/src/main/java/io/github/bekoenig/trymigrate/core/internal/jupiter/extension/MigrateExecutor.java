package io.github.bekoenig.trymigrate.core.internal.jupiter.extension;

import io.github.bekoenig.trymigrate.core.TrymigrateTest;
import io.github.bekoenig.trymigrate.core.config.TrymigrateDataLoadHandle;
import io.github.bekoenig.trymigrate.core.internal.flyway.FlywayMigrateWrapper;
import io.github.bekoenig.trymigrate.core.internal.flyway.callback.DataLoader;
import io.github.bekoenig.trymigrate.core.internal.jupiter.StoreSupport;
import io.github.bekoenig.trymigrate.core.internal.schemacrawler.lint.LintPattern;
import io.github.bekoenig.trymigrate.core.internal.schemacrawler.lint.LintPatterns;
import io.github.bekoenig.trymigrate.core.lint.AcceptLint;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.MigrationVersion;
import org.flywaydb.core.api.callback.Callback;
import org.flywaydb.core.api.configuration.FluentConfiguration;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.platform.commons.support.AnnotationSupport;

import java.util.List;
import java.util.Optional;

import static io.github.bekoenig.trymigrate.core.config.TrymigrateFlywayConfigurer.addCallbacks;

public class MigrateExecutor implements BeforeEachCallback {

    @Override
    public void beforeEach(ExtensionContext extensionContext) {
        // store global state in parent
        FlywayMigrateWrapper flywayMigrateWrapper = new FlywayMigrateWrapper(extensionContext.getParent()
                .orElseThrow());
        if (flywayMigrateWrapper.isLatest()) {
            return;
        }

        FluentConfiguration fluentConfiguration = StoreSupport.getFlywayConfigurationFactory(extensionContext).get();
        Optional<TrymigrateTest> flywayMigrationTest = AnnotationSupport.findAnnotation(
                extensionContext.getElement().orElseThrow(), TrymigrateTest.class);

        boolean cleanBefore;
        if (flywayMigrationTest.isPresent()) {
            fluentConfiguration.target(flywayMigrationTest.get().whenTarget());

            if (flywayMigrationTest.get().givenData().length > 0) {
                Callback callback = new DataLoader(
                        StoreSupport.getBeanProvider(extensionContext).all(TrymigrateDataLoadHandle.class),
                        MigrationVersion.fromVersion(flywayMigrationTest.get().whenTarget()),
                        List.of(flywayMigrationTest.get().givenData())
                );
                addCallbacks(fluentConfiguration, List.of(callback));
            }

            cleanBefore = flywayMigrationTest.get().cleanBefore();
        } else {
            fluentConfiguration.target(MigrationVersion.LATEST);
            cleanBefore = false;
        }

        Flyway flyway = fluentConfiguration.load();
        if (cleanBefore) {
            flyway.clean();
        }

        flywayMigrateWrapper.migrate(flyway, acceptedLintsFromAnnotation(extensionContext));
    }

    private LintPatterns acceptedLintsFromAnnotation(ExtensionContext extensionContext) {
        return new LintPatterns(AnnotationSupport.findRepeatableAnnotations(
                        extensionContext.getElement().orElseThrow(), AcceptLint.class).stream()
                .map(acceptLint -> new LintPattern(acceptLint.linterId(), acceptLint.objectName())).toList());
    }

}
