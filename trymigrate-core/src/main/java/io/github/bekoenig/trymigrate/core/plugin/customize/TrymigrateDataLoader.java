package io.github.bekoenig.trymigrate.core.plugin.customize;

import io.github.bekoenig.trymigrate.core.TrymigrateGivenData;

import java.sql.Connection;

/**
 * Plugin interface for custom seed data loaders.
 * <p>
 * Implementations of this interface handle the loading of data specified via the
 * {@link TrymigrateGivenData} annotation. By default, trymigrate executes raw SQL strings.
 * This interface allows you to support custom formats or complex loading logic.
 * <p>
 * <b>How it works:</b>
 * For every entry in a {@code @TrymigrateGivenData} annotation, all registered loaders are
 * queried via {@link #supports(String, String, TrymigrateDatabase)}. The first loader that
 * returns {@code true} is responsible for loading that specific resource.
 * <p>
 * <b>Custom Use Cases:</b>
 * <ul>
 *     <li>Load data from CSV or JSON files.</li>
 *     <li>Integrate with data generation tools (e.g., Datafaker).</li>
 *     <li>Execute complex setup logic using a custom Java-based DSL.</li>
 * </ul>
 *
 * @see TrymigrateGivenData
 * @see TrymigrateDatabase
 */
public interface TrymigrateDataLoader {

    /**
     * Determines if this loader can handle the specified resource.
     *
     * @param resource  the data resource (e.g., a file path or a raw string)
     * @param extension the file extension of the resource (if available, without dot), or {@code null}
     * @param database  the current test database instance
     * @return {@code true} if this loader can process the resource, {@code false} otherwise
     */
    boolean supports(String resource, String extension, TrymigrateDatabase database);

    /**
     * Executes the data loading logic.
     *
     * @param resource   the data resource to load
     * @param connection the active JDBC connection to the test database
     * @param database   the test database instance (providing access to vendor-specific containers)
     */
    void load(String resource, Connection connection, TrymigrateDatabase database);

}
