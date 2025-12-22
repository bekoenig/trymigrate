package io.github.bekoenig.trymigrate.core.lint;

import java.lang.annotation.*;

/**
 * Marker annotation to suppress lints for test method. Use this annotation to allowlist lints which break the
 * quality gate of {@link TrymigrateAssertLints#failOn()}. The lints are not removed. They are still included in the
 * report and in the method parameter set for further asserts.
 * <p>
 * Supports regex to accept multiple lints using single annotation.
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Repeatable(TrymigrateSuppressLint.TrymigrateSuppressLints.class)
public @interface TrymigrateSuppressLint {

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

    /**
     * Meta annotation to add support for repeatable usage of {@link TrymigrateSuppressLint}.
     * <p>
     * Only necessary for compile time.
     */
    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @interface TrymigrateSuppressLints {

        TrymigrateSuppressLint[] value();

    }

}
