package io.github.bekoenig.trymigrate.core.plugin.customize;

import java.util.Optional;

/**
 * Abstraction for the database used during migration testing.
 * <p>
 * This interface provides trymigrate with the necessary connection details and lifecycle
 * hooks to manage the test database.
 * <p>
 * <b>Standard Implementation:</b>
 * The most common way to use this is through trymigrate's automatic wrapping of
 * Testcontainers' {@code JdbcDatabaseContainer} via {@link io.github.bekoenig.trymigrate.core.plugin.TrymigrateRegisterPlugin}.
 * <p>
 * <b>Custom Implementation:</b>
 * You can implement this interface to connect trymigrate to:
 * <ul>
 *     <li>A shared external database instance.</li>
 *     <li>A custom-managed container lifecycle.</li>
 *     <li>An in-memory database like H2 or HSQLDB.</li>
 * </ul>
 * <p>
 * Register a database locally via
 * {@link io.github.bekoenig.trymigrate.core.plugin.TrymigrateRegisterPlugin}, or make it globally discoverable by
 * implementing {@link io.github.bekoenig.trymigrate.core.plugin.TrymigratePlugin} as well.
 *
 * @see io.github.bekoenig.trymigrate.core.plugin.TrymigrateRegisterPlugin
 */
public interface TrymigrateDatabase {

    /**
     * Provides access to the underlying database instance if it matches the given type.
     * <p>
     * This is useful for accessing vendor-specific features of a database container.
     *
     * @param <T>  the target type
     * @param type the class of the target type
     * @return an {@link Optional} with the instance, or empty if the type doesn't match
     */
    default <T> Optional<T> unwrap(Class<T> type) {
        if (type.isInstance(this)) {
            return Optional.of(type.cast(this));
        }
        return Optional.empty();
    }

    /**
     * Prepares the database for use.
     * <p>
     * This method is called exactly once before any migrations are executed.
     * For containers, this typically triggers the {@code start()} method.
     */
    void prepare();

    /**
     * Returns the JDBC URL for the database connection.
     *
     * @return the JDBC connection string
     */
    String getJdbcUrl();

    /**
     * Returns the username for the database connection.
     *
     * @return the database username
     */
    String getUsername();

    /**
     * Returns the password for the database connection.
     *
     * @return the database password
     */
    String getPassword();

    /**
     * Disposes of the database after use.
     * <p>
     * This method is called when the database is no longer needed.
     * For containers, this typically triggers the {@code stop()} method.
     */
    void dispose();

}
