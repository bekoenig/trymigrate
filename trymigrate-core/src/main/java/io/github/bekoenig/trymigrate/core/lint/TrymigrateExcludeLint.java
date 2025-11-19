package io.github.bekoenig.trymigrate.core.lint;

import java.lang.annotation.*;

/**
 * Marker annotation to exclude lints for a test class. Excluded lints are completely dropped and not reported or
 * fail the quality gate.
 * <p>
 * Supports regex to ignore multiple lints using single annotation.
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(TrymigrateExcludeLint.TrymigrateExcludeLints.class)
public @interface TrymigrateExcludeLint {

    /**
     * Linter-id of the lint to exclude.
     *
     * @return linter-id
     */
    String linterId() default ".*";

    /**
     * Object name of the lint to exclude.
     *
     * @return object name
     */
    String objectName() default ".*";

    /**
     * Meta annotation to add support for repeatable usage of {@link TrymigrateExcludeLint}.
     * <p>
     * Only necessary for compile time.
     */
    @Target({ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @interface TrymigrateExcludeLints {

        TrymigrateExcludeLint[] value();

    }

}
