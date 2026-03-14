package io.github.bekoenig.trymigrate.core.lint;

import java.lang.annotation.*;

/**
 * Suppresses specific lints locally for a single migration version.
 * <p>
 * This annotation is used to "allowlist" certain schema violations that would otherwise trigger
 * a quality gate failure via {@link TrymigrateVerifyLints}. Unlike {@link TrymigrateExcludeLint},
 * these lints are <b>not</b> removed; they still appear in HTML reports and are available
 * in the injected {@link schemacrawler.tools.lint.Lints} parameter for further manual assertion.
 * <p>
 * <b>Note:</b> The Flyway migration history table is explicitly excluded by default and does not require
 * suppression.
 * <p>
 * <b>Verification Points and Multiple Tests:</b>
 * Quality gate verification is only performed the <b>first time</b> a migration version is
 * reached (based on test execution order). If you have multiple tests for the same target
 * version (e.g., to verify different data scenarios), you only need to apply this annotation
 * to the first test method. Applying it to subsequent tests for the same version will have
 * no effect, as the quality gate has already been verified for this version.
 * <p>
 * <b>Regex Support:</b>
 * Both {@link #linterId()} and {@link #objectName()} support regular expressions.
 * <p>
 * <b>Example:</b>
 * <pre>{@code
 * @Test
 * @TrymigrateWhenTarget("1.1")
 * @TrymigrateSuppressLint(linterId = "LinterTableWithNoRemarks", objectName = "legacy_.*")
 * void should_MigrateLegacyTables(Catalog catalog, Lints lints) {
 *     // Lints for legacy_tables will not fail the test, but remain in 'lints'
 *     assertThat(catalog).table("legacy_user").exists();
 * }
 * }</pre>
 *
 * @see TrymigrateVerifyLints
 * @see TrymigrateExcludeLint
 */
@Target({ElementType.ANNOTATION_TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Repeatable(TrymigrateSuppressLint.TrymigrateSuppressLints.class)
public @interface TrymigrateSuppressLint {

    /**
     * The ID of the linter to suppress.
     * <p>
     * Supports regular expressions. Defaults to {@code ".*"} (match all linters).
     *
     * @return linter ID pattern
     */
    String linterId() default ".*";

    /**
     * The name of the database object (e.g., table or column) to suppress.
     * <p>
     * Supports regular expressions. Defaults to {@code ".*"} (match all objects).
     *
     * @return object name pattern
     */
    String objectName() default ".*";

    /**
     * Meta annotation to add support for repeatable usage of {@link TrymigrateSuppressLint}.
     */
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @interface TrymigrateSuppressLints {

        TrymigrateSuppressLint[] value();

    }

}
