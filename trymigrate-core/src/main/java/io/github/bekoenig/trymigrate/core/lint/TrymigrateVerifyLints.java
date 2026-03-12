package io.github.bekoenig.trymigrate.core.lint;

import schemacrawler.tools.lint.LintSeverity;

import java.lang.annotation.*;

/**
 * Activates the linting quality gate for the test class.
 * <p>
 * This annotation tells trymigrate to automatically verify the database schema against best practices
 * (using SchemaCrawler) after each migration. If new violations are detected that meet or exceed
 * the specified severity threshold, the test will fail.
 * <p>
 * <b>Smart Diffing:</b>
 * Verification only fails for <b>new</b> lints introduced by the current migration version.
 * Violations already present in previous versions are ignored to avoid noise from legacy schema issues.
 * <p>
 * <b>Example:</b>
 * <pre>{@code
 * @TrymigrateVerifyLints(failOn = LintSeverity.high)
 * class MySchemaTest { ... }
 * }</pre>
 *
 * @see TrymigrateExcludeLint
 * @see TrymigrateSuppressLint
 * @see schemacrawler.tools.lint.LintSeverity
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface TrymigrateVerifyLints {

    /**
     * The severity threshold at which a detected lint will cause the test to fail.
     * <p>
     * For example, if set to {@link LintSeverity#high}, any detected lint with severity
     * {@code high} or {@code critical} will trigger a failure.
     *
     * @return the minimum severity level to cause a failure
     */
    LintSeverity failOn();

}
