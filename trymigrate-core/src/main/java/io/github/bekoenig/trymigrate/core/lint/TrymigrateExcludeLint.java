package io.github.bekoenig.trymigrate.core.lint;

import java.lang.annotation.*;

/**
 * Excludes specific lints globally for the entire test class.
 * <p>
 * Matching lints are completely dropped from processing. They do not appear in HTML reports,
 * are not printed to the console, do not trigger a quality gate failure, and are not present
 * in the injected {@link schemacrawler.tools.lint.Lints} test parameter.
 * <p>
 * This is useful for ignoring persistent issues in legacy schemas or excluding rules that
 * are not relevant to your project's standards.
 * <p>
 * <b>Note:</b> Linting is restricted to Flyway-managed schemas. The Flyway migration history table is
 * already excluded by default. Use this annotation to exclude additional application-specific objects.
 * <p>
 * <b>Regex Support:</b>
 * Both {@link #linterId()} and {@link #objectName()} support regular expressions.
 * <p>
 * <b>Example:</b>
 * <pre>{@code
 * @TrymigrateExcludeLint(linterId = ".*remarks.*", objectName = "TEMP_.*")
 * class MySchemaTest { ... }
 * }</pre>
 *
 * @see TrymigrateVerifyLints
 * @see TrymigrateSuppressLint
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Repeatable(TrymigrateExcludeLint.TrymigrateExcludeLints.class)
public @interface TrymigrateExcludeLint {

    /**
     * The ID of the linter to exclude.
     * <p>
     * Supports regular expressions. Defaults to {@code ".*"} (match all linters).
     *
     * @return linter ID pattern
     */
    String linterId() default ".*";

    /**
     * The name of the database object (e.g., table or column) to exclude.
     * <p>
     * Supports regular expressions. Defaults to {@code ".*"} (match all objects).
     *
     * @return object name pattern
     */
    String objectName() default ".*";

    /**
     * Meta annotation to add support for repeatable usage of {@link TrymigrateExcludeLint}.
     */
    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @Inherited
    @interface TrymigrateExcludeLints {

        TrymigrateExcludeLint[] value();

    }

}
