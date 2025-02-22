package io.github.bekoenig.trymigrate.core.internal.schemacrawler.lint;

import schemacrawler.tools.lint.Lint;

import java.io.Serializable;
import java.util.List;

public class LintPatterns {

    public static final LintPatterns EMPTY = new LintPatterns(List.of());

    private final List<LintPattern> lintPatterns;

    public LintPatterns(List<LintPattern> lintPatterns) {
        this.lintPatterns = lintPatterns;
    }

    public boolean matches(Lint<? extends Serializable> lint) {
        return lintPatterns.stream().anyMatch(lintPattern -> lintPattern.matches(lint));
    }

    public boolean notMatches(Lint<? extends Serializable> lint) {
        return !matches(lint);
    }

}
