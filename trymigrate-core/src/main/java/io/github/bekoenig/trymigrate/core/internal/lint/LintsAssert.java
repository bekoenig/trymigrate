package io.github.bekoenig.trymigrate.core.internal.lint;

import io.github.bekoenig.trymigrate.core.lint.TrymigrateExcludeLint;
import io.github.bekoenig.trymigrate.core.lint.TrymigrateSuppressLint;
import io.github.bekoenig.trymigrate.core.plugin.customize.TrymigrateLintersConfigurer;
import io.github.bekoenig.trymigrate.core.plugin.TrymigrateBean;
import schemacrawler.tools.lint.Lint;
import schemacrawler.tools.lint.LintSeverity;
import schemacrawler.tools.lint.Lints;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class LintsAssert {

    private final LintSeverity failOn;

    public LintsAssert(LintSeverity failOn) {
        this.failOn = failOn;
    }

    private boolean isDisabled() {
        return Objects.isNull(failOn);
    }

    public void assertLints(Lints lints, LintPatterns suppressedLintPatterns) {
        if (isDisabled()) {
            return;
        }

        List<Lint<? extends Serializable>> assertLints = suppressedLintPatterns
                .dropMatching(lints.getLints().stream())
                .filter(x -> hasOrExceedsSeverity(x, failOn))
                .toList();

        if (assertLints.isEmpty()) {
            return;
        }

        throw new AssertionError(MessageFormat.format("""
                Some lints have exceeded the quality threshold ({0}):
                
                {1}
                
                What can you do?
                
                For a new migration:
                  Try to improve it
                For an already applied migration, or if this rule needs to be ignored once for a valid reason:
                  Use @{2} at the method level to suppress the rule for the failing test method
                For general or partial rejection of this rule:
                  Use @{3} at the class level to exclude this rule for the entire test class
                
                Note: If you want to customize the lint profile (e.g. severity, affected database objects, ...), add
                a field annotated with @{4} to test class or a custom plugin and implement a {5}
                """,
                failOn.name(),
                assertLints.stream()
                        .map(x -> x.getLinterId() + ": " + x)
                        .collect(Collectors.joining(System.lineSeparator())),
                TrymigrateSuppressLint.class.getSimpleName(),
                TrymigrateExcludeLint.class.getSimpleName(),
                TrymigrateBean.class.getSimpleName(),
                TrymigrateLintersConfigurer.class.getSimpleName()));
    }

    protected static boolean hasOrExceedsSeverity(Lint<? extends Serializable> lint, LintSeverity threshold) {
        return lint.getSeverity().ordinal() >= threshold.ordinal();
    }

}
