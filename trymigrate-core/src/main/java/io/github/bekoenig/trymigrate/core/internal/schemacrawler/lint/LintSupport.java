package io.github.bekoenig.trymigrate.core.internal.schemacrawler.lint;

import schemacrawler.tools.lint.Lint;
import schemacrawler.tools.lint.LintSeverity;

import java.io.Serializable;

public class LintSupport {

    private LintSupport() {
    }

    public static boolean hasOrExceedsSeverity(Lint<? extends Serializable> lint, LintSeverity threshold) {
        return lint.getSeverity().ordinal() >= threshold.ordinal();
    }

}
