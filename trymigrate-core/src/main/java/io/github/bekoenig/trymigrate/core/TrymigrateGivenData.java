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
 * <b>Plain Execution:</b>
 * Data loading via this annotation is performed independently of Flyway's SQL processing.
 * Standard Flyway features like <b>placeholders</b>, <b>default schema selection</b>, or
 * <b>vendor-specific script enhancements</b> are NOT supported. SQL must be provided in
 * its final, executable form (e.g., using schema-qualified table names).
 * <p>
 * The value can be a resource path (e.g., "db/testdata/initial.sql") or a raw SQL string,
 * depending on the supported types of registered
 * {@link io.github.bekoenig.trymigrate.core.plugin.customize.TrymigrateDataLoader} plugins.
 * <p>
 * <b>Default SQL Loader:</b> The built-in loader for {@code .sql} files expects a raw classpath
 * path <b>without</b> a prefix (e.g., do NOT use {@code classpath:}).
 * <p>
 * <b>Inline SQL Fallback:</b> If a string does not match any registered loader (e.g., it doesn't end in {@code .sql}),
 * it is executed as a raw SQL statement via JDBC. This is useful for quick inserts or setups.
 * <p>
 * <b>Execution Order:</b> The resources/statements are processed in the exact order they are defined in the array.
 */
@Target({ElementType.ANNOTATION_TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TrymigrateGivenData {

    /**
     * List of resources or inline SQL statements to load.
     *
     * @return resources as input for {@link TrymigrateDataLoader} or raw SQL.
     */
    String[] value();

}
