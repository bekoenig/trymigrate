package io.github.bekoenig.trymigrate.core.internal.schemacrawler.lint;

import schemacrawler.tools.lint.Lint;
import schemacrawler.tools.lint.LintSeverity;
import schemacrawler.tools.lint.Lints;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

public class LintsAssert {

    private final LintSeverity failOn;

    public LintsAssert(LintSeverity failOn) {
        this.failOn = failOn;
    }

    public void assertLints(Lints lints, LintPatterns acceptedLints) {
        List<Lint<? extends Serializable>> assertLints = lints.stream()
                .filter(acceptedLints::notMatches)
                // TODO: Write test to ensure stable ordinal
                .filter(x -> x.getSeverity().ordinal() >= failOn.ordinal())
                .toList();

        if (!assertLints.isEmpty()) {
            throw new AssertionError("Fix or accept lints:\n\n" + format(assertLints));
        }
    }

    private String format(List<Lint<? extends Serializable>> newLintsOverLimit) {
        return newLintsOverLimit.stream()
                .map(x -> x.getLinterId() + ": " + x.getObjectName())
                .collect(Collectors.joining("\n"));
    }


}
