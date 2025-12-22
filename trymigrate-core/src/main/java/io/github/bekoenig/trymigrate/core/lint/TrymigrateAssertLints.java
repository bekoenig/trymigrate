package io.github.bekoenig.trymigrate.core.lint;

import schemacrawler.tools.lint.LintSeverity;

import java.lang.annotation.*;

/**
 * Enables lints assertion after all migrations are applied.
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface TrymigrateAssertLints {

    /**
     * Threshold to fail on lints. Indicates mistakes in the database model on migration.
     *
     * @see TrymigrateExcludeLint
     * @see TrymigrateSuppressLint
     * @return lower boundary
     */
    LintSeverity failOn();

}
