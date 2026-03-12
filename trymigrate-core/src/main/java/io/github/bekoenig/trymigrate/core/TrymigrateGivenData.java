package io.github.bekoenig.trymigrate.core;

import io.github.bekoenig.trymigrate.core.plugin.customize.TrymigrateDataLoader;

import java.lang.annotation.*;

/**
 * Loads scenario data into the database state before the target migration version is applied.
 * <p>
 * The data is loaded during the Flyway lifecycle ({@code Event.BEFORE_EACH_MIGRATE}) for the specific
 * target version. This allows testing how the target migration script handles existing data.
 * <p>
 * <b>Note:</b> This requires that the migration to the target version actually takes place.
 * If the database is already at or above the target version, an {@link IllegalStateException}
 * will be thrown. Use {@link TrymigrateCleanBefore} to ensure a fresh state if necessary.
 * <p>
 * The value can be a resource path (e.g., "db/testdata/initial.sql") or a raw SQL string,
 * depending on the supported types of registered
 * {@link io.github.bekoenig.trymigrate.core.plugin.customize.TrymigrateDataLoader} plugins.
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
