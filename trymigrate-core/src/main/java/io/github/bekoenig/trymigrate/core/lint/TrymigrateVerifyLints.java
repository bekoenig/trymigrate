package io.github.bekoenig.trymigrate.core.lint;

import schemacrawler.tools.lint.LintSeverity;

import java.lang.annotation.*;

/**
 * Enables lint verification when a new target version is applied for the first time.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface TrymigrateVerifyLints {

    /**
     * Threshold to fail on lints. Indicates mistakes in the database model on migration.
     *
     * @see TrymigrateExcludeLint
     * @see TrymigrateSuppressLint
     * @return lower boundary
     */
    LintSeverity failOn();

}
