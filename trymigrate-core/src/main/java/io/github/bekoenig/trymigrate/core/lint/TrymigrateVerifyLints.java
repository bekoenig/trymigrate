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
 * <b>Verification Points &#38; Smart Diffing:</b>
 * This annotation <b>uses</b> the Verification Points defined by {@link io.github.bekoenig.trymigrate.core.TrymigrateWhenTarget}
 * for smart diffing. The quality gate only fails for <b>new</b> lints introduced since the previous
 * Verification Point, ignoring pre-existing violations to avoid noise from legacy schema issues.
 * <p>
 * <b>Important Notes:</b>
 * <ul>
 *     <li><b>Flyway History:</b> The Flyway schema history table is automatically excluded from verification to prevent
 *     false positives.</li>
 *     <li><b>Schema Scope:</b> Verification is strictly limited to schemas managed by Flyway. Issues in other schemas
 *     are not detected.</li>
 * </ul>
 * <p>
 * <b>Intermediate Reporting:</b>
 * Even if a test method skips several migration versions (e.g., from 1.0 to 1.3), trymigrate
 * still performs linting and generates reports for every intermediate migration version (1.1, 1.2, etc.).
 * However, the quality gate verification only occurs once the migration to the target version
 * of the test method is complete.
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
 * @see io.github.bekoenig.trymigrate.core.plugin.customize.TrymigrateLintOptionsCustomizer
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
