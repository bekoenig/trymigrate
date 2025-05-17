package io.github.bekoenig.trymigrate.core.internal.lint;

import schemacrawler.tools.lint.Lint;

import java.io.Serializable;

public record LintPattern(String linterId, String objectName) {

    public boolean matches(Lint<? extends Serializable> lint) {
        return lint.getLinterId().matches(linterId) && lint.getObjectName().matches(objectName);
    }

}
