package io.github.bekoenig.trymigrate.core.lint;

import java.lang.annotation.*;

/**
 * Marker annotation to exclude lints for a test class. Excluded lints will not be reported or fail a test method.
 * <p>
 * Supports regex to ignore multiple lints using single annotation.
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(TrymigrateExcludeLints.class)
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

}
