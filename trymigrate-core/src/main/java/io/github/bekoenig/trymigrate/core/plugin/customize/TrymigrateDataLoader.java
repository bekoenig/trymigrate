package io.github.bekoenig.trymigrate.core.plugin.customize;

import io.github.bekoenig.trymigrate.core.TrymigrateGivenData;

import java.sql.Connection;

/**
 * Handler to load data given by {@link TrymigrateGivenData}.
 */
public interface TrymigrateDataLoader {

    /**
     * Checks support of current data loader for a given resource.
     *
     * @param resource  resource for data
     * @param extension optional extension of resource on path (maybe {@code null})
     * @param database  optional database (maybe {@code null})
     * @return {@code true} when supported
     */
    boolean supports(String resource, String extension, TrymigrateDatabase database);

    /**
     * Loads data resource.
     *
     * @param resource   resource for data
     * @param connection current database connection
     * @param database   optional database (maybe {@code null})
     */
    void load(String resource, Connection connection, TrymigrateDatabase database);

}
