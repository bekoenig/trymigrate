package io.github.bekoenig.trymigrate.core;

import io.github.bekoenig.trymigrate.core.plugin.customize.TrymigrateDataLoader;

import java.lang.annotation.*;

/**
 * Loads scenario data before execution of migration to target version.
 */
@Target({ElementType.ANNOTATION_TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TrymigrateGivenData {

    /**
     * List of resources to load.
     *
     * @return resources as input for {@link TrymigrateDataLoader}
     */
    String[] value();

}
