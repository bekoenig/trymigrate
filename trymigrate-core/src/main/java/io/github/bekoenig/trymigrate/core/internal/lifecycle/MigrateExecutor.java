package io.github.bekoenig.trymigrate.core.internal.lifecycle;

import io.github.bekoenig.trymigrate.core.TrymigrateTest;
import io.github.bekoenig.trymigrate.core.plugin.customize.TrymigrateDataLoader;
import io.github.bekoenig.trymigrate.core.internal.migrate.FlywayMigrateWrapper;
import io.github.bekoenig.trymigrate.core.internal.migrate.callback.DataLoader;
import io.github.bekoenig.trymigrate.core.internal.StoreSupport;
import io.github.bekoenig.trymigrate.core.internal.lint.LintPattern;
import io.github.bekoenig.trymigrate.core.internal.lint.LintPatterns;
import io.github.bekoenig.trymigrate.core.lint.TrymigrateSuppressLint;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.MigrationVersion;
import org.flywaydb.core.api.callback.Callback;
import org.flywaydb.core.api.configuration.FluentConfiguration;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.platform.commons.support.AnnotationSupport;

import java.util.List;
import java.util.Optional;

import static io.github.bekoenig.trymigrate.core.plugin.customize.TrymigrateFlywayCustomizer.addCallbacks;

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
                extensionContext.getRequiredTestMethod(), TrymigrateTest.class);

        boolean cleanBefore;
        if (flywayMigrationTest.isPresent()) {
            fluentConfiguration.target(flywayMigrationTest.get().whenTarget());

            Callback callback = new DataLoader(
                    StoreSupport.getBeanProvider(extensionContext).all(TrymigrateDataLoader.class),
                    MigrationVersion.fromVersion(flywayMigrationTest.get().whenTarget()),
                    List.of(flywayMigrationTest.get().givenData())
            );
            addCallbacks(fluentConfiguration, List.of(callback));

            cleanBefore = flywayMigrationTest.get().cleanBefore();
        } else {
            fluentConfiguration.target(MigrationVersion.LATEST);
            cleanBefore = false;
        }

        Flyway flyway = fluentConfiguration.load();
        flywayMigrateWrapper.migrate(flyway, cleanBefore, suppressedLintPatternsFromAnnotation(extensionContext));
    }

    private LintPatterns suppressedLintPatternsFromAnnotation(ExtensionContext extensionContext) {
        return new LintPatterns(AnnotationSupport.findRepeatableAnnotations(
                        extensionContext.getRequiredTestMethod(), TrymigrateSuppressLint.class).stream()
                .map(suppressLint -> new LintPattern(suppressLint.linterId(), suppressLint.objectName())).toList());
    }

}
