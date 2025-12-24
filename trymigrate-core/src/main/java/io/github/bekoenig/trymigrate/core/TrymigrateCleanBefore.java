package io.github.bekoenig.trymigrate.core;

import java.lang.annotation.*;

/**
 * Runs clean before migration.
 */
@Target({ElementType.ANNOTATION_TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TrymigrateCleanBefore {
}
