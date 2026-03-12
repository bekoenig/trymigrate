package io.github.bekoenig.trymigrate.core.lint;

import java.lang.annotation.*;

/**
 * Excludes specific lints globally for the entire test class.
 * <p>
 * Lints matching the criteria defined in this annotation are completely dropped. They will not appear
 * in HTML reports, will not be printed to the console, and will not trigger a quality gate failure.
 * <p>
 * This is useful for ignoring persistent issues in legacy schemas or excluding specific rules that
 * are not relevant to your project's standards.
 * <p>
 * <b>Regex Support:</b>
 * Both {@link #linterId()} and {@link #objectName()} support regular expressions, allowing you to
 * match multiple linters or objects with a single annotation.
 * <p>
 * <b>Example:</b>
 * <pre>{@code
 * // Exclude all "remarks" lints for any table starting with "TEMP_"
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
