package io.github.bekoenig.trymigrate.core.internal.schemacrawler.lint;

import schemacrawler.tools.lint.Lint;
import schemacrawler.tools.lint.LintSeverity;
import schemacrawler.tools.lint.Lints;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.List;
import java.util.stream.Collectors;

public class LintsAssert {

    private final LintSeverity failOn;

    public LintsAssert(LintSeverity failOn) {
        this.failOn = failOn;
    }

    public void assertLints(Lints lints, LintPatterns acceptedLints) {
        List<Lint<? extends Serializable>> assertLints = acceptedLints
                .dropMatching(lints.getLints().stream())
                .filter(x -> LintSupport.hasOrExceedsSeverity(x, failOn))
                .toList();

        if (assertLints.isEmpty()) {
            return;
        }

        throw new AssertionError(MessageFormat.format("Fix or accept lints: {0}{1}",
                System.lineSeparator(),
                lints.stream()
                        .map(x -> x.getLinterId() + ": " + x)
                        .collect(Collectors.joining(System.lineSeparator()))));
    }

}
