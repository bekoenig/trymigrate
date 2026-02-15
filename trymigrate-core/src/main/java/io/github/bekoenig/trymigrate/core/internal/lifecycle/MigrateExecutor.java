package io.github.bekoenig.trymigrate.core.internal.lifecycle;

import io.github.bekoenig.trymigrate.core.TrymigrateCleanBefore;
import io.github.bekoenig.trymigrate.core.TrymigrateGivenData;
import io.github.bekoenig.trymigrate.core.TrymigrateWhenTarget;
import io.github.bekoenig.trymigrate.core.internal.StoreSupport;
import io.github.bekoenig.trymigrate.core.internal.lint.LintPattern;
import io.github.bekoenig.trymigrate.core.internal.lint.LintPatterns;
import io.github.bekoenig.trymigrate.core.lint.TrymigrateSuppressLint;
import org.flywaydb.core.api.MigrationVersion;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.platform.commons.support.AnnotationSupport;

import java.util.List;

public class MigrateExecutor implements BeforeEachCallback {

    @Override
    public void beforeEach(ExtensionContext extensionContext) {
        MigrationVersion target = AnnotationSupport.findAnnotation(
                        extensionContext.getRequiredTestMethod(), TrymigrateWhenTarget.class)
                .map(TrymigrateWhenTarget::value)
                .map(MigrationVersion::fromVersion)
                .orElse(null);

        List<String> resources = AnnotationSupport.findAnnotation(
                        extensionContext.getRequiredTestMethod(), TrymigrateGivenData.class)
                .map(TrymigrateGivenData::value)
                .map(List::of)
                .orElse(List.of());

        boolean cleanBefore = AnnotationSupport.findAnnotation(
                        extensionContext.getRequiredTestMethod(), TrymigrateCleanBefore.class)
                .isPresent();

        LintPatterns suppressedLintPatterns = new LintPatterns(AnnotationSupport.findRepeatableAnnotations(
                        extensionContext.getRequiredTestMethod(), TrymigrateSuppressLint.class).stream()
                .map(x -> new LintPattern(x.linterId(), x.objectName())).toList());

        StoreSupport.getMigrateProcessor(extensionContext)
                .migrate(target, resources, cleanBefore, suppressedLintPatterns);
    }

}
