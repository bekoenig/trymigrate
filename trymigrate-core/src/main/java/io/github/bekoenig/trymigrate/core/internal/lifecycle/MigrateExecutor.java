package io.github.bekoenig.trymigrate.core.internal.lifecycle;

import io.github.bekoenig.trymigrate.core.TrymigrateTest;
import io.github.bekoenig.trymigrate.core.internal.StoreSupport;
import io.github.bekoenig.trymigrate.core.internal.lint.LintPattern;
import io.github.bekoenig.trymigrate.core.internal.lint.LintPatterns;
import io.github.bekoenig.trymigrate.core.internal.migrate.FlywayMigrateWrapper;
import io.github.bekoenig.trymigrate.core.internal.migrate.callback.DataLoader;
import io.github.bekoenig.trymigrate.core.lint.TrymigrateSuppressLint;
import io.github.bekoenig.trymigrate.core.plugin.customize.TrymigrateDataLoader;
import org.flywaydb.core.api.MigrationVersion;
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

        Optional<TrymigrateTest> trymigrateTest = AnnotationSupport.findAnnotation(
                extensionContext.getRequiredTestMethod(), TrymigrateTest.class);

        MigrationVersion target = trymigrateTest
                .map(TrymigrateTest::whenTarget).map(MigrationVersion::fromVersion)
                .orElse(MigrationVersion.LATEST);

        FluentConfiguration fluentConfiguration = StoreSupport.getFlywayConfigurationFactory(extensionContext).get();

        fluentConfiguration.target(target);

        addCallbacks(fluentConfiguration, List.of(new DataLoader(
                StoreSupport.getBeanProvider(extensionContext).all(TrymigrateDataLoader.class),
                target,
                trymigrateTest.map(TrymigrateTest::givenData).map(List::of).orElse(List.of())
        )));

        flywayMigrateWrapper.migrate(fluentConfiguration.load(),
                trymigrateTest.map(TrymigrateTest::cleanBefore).orElse(false),
                suppressedLintPatternsFromAnnotation(extensionContext));
    }

    private LintPatterns suppressedLintPatternsFromAnnotation(ExtensionContext extensionContext) {
        return new LintPatterns(AnnotationSupport.findRepeatableAnnotations(
                        extensionContext.getRequiredTestMethod(), TrymigrateSuppressLint.class).stream()
                .map(suppressLint ->
                        new LintPattern(suppressLint.linterId(), suppressLint.objectName())).toList());
    }

}
