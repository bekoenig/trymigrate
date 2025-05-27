package io.github.bekoenig.trymigrate.core.internal.lifecycle;

import io.github.bekoenig.trymigrate.core.TrymigrateTest;
import io.github.bekoenig.trymigrate.core.internal.StoreSupport;
import io.github.bekoenig.trymigrate.core.internal.lint.LintPattern;
import io.github.bekoenig.trymigrate.core.internal.lint.LintPatterns;
import io.github.bekoenig.trymigrate.core.internal.migrate.MigrateProcessor;
import io.github.bekoenig.trymigrate.core.lint.TrymigrateSuppressLint;
import org.flywaydb.core.api.MigrationVersion;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.platform.commons.support.AnnotationSupport;

import java.util.List;
import java.util.Optional;

public class MigrateExecutor implements BeforeEachCallback {

    @Override
    public void beforeEach(ExtensionContext extensionContext) {
        MigrateProcessor migrateProcessor = StoreSupport.getMigrateProcessor(extensionContext);

        Optional<TrymigrateTest> trymigrateTest = AnnotationSupport.findAnnotation(
                extensionContext.getRequiredTestMethod(), TrymigrateTest.class);

        boolean cleanBefore = trymigrateTest
                .map(TrymigrateTest::cleanBefore)
                .orElse(false);

        List<String> resources = trymigrateTest
                .map(TrymigrateTest::givenData).map(List::of)
                .orElse(List.of());

        MigrationVersion target = trymigrateTest
                .map(TrymigrateTest::whenTarget).map(MigrationVersion::fromVersion)
                .orElse(MigrationVersion.LATEST);

        LintPatterns suppressedLintPatterns = new LintPatterns(AnnotationSupport.findRepeatableAnnotations(
                        extensionContext.getRequiredTestMethod(), TrymigrateSuppressLint.class).stream()
                .map(x -> new LintPattern(x.linterId(), x.objectName())).toList());

        migrateProcessor.migrate(target, resources, cleanBefore, suppressedLintPatterns);
    }

}
