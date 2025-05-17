package io.github.bekoenig.trymigrate.core.lint;

import io.github.bekoenig.trymigrate.core.Trymigrate;

import java.lang.annotation.*;

/**
 * Marker annotation to suppress lints for test method. Use this annotation to whitelist lints which break the
 * quality gate of {@link Trymigrate#failOn()}.
 * <p>
 * Supports regex to accept multiple lints using single annotation.
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(SuppressLints.class)
public @interface SuppressLint {

    /**
     * Linter-id of the lint to suppress.
     *
     * @return linter-id
     */
    String linterId() default ".*";

    /**
     * Object name of the lint to suppress.
     *
     * @return object name
     */
    String objectName() default ".*";

}
