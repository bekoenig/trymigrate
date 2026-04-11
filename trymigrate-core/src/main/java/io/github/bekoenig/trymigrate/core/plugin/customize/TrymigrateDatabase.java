package io.github.bekoenig.trymigrate.core.plugin.customize;

import java.util.Optional;

/**
 * Abstraction for the database used during migration testing.
 * <p>
 * This interface provides trymigrate with the necessary connection details and lifecycle
 * hooks to manage the test database.
 * trymigrate uses the JDBC URL, username, and password from a registered
 * {@code TrymigrateDatabase} as Flyway's default {@code DataSource}.
 * A {@link TrymigrateFlywayCustomizer} can further customize or override that Flyway configuration.
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
 * <p>
 * <b>Constraint:</b> Exactly one database plugin may be active for a test class. trymigrate fails fast if
 * multiple {@code TrymigrateDatabase} implementations are registered at the same time.
 *
 * @see io.github.bekoenig.trymigrate.core.plugin.TrymigrateRegisterPlugin
 */
public interface TrymigrateDatabase {

    /**
     * Creates a simple database adapter from static JDBC connection details.
     * <p>
     * This is a convenience factory for databases whose lifecycle is managed externally
     * or does not require explicit setup and teardown. The returned implementation uses
     * no-op lifecycle hooks for {@link #prepare()} and {@link #dispose()}.
     *
     * @param jdbcUrl  the JDBC connection string
     * @param username the database username
     * @param password the database password
     * @return a {@link TrymigrateDatabase} backed by the provided connection details
     */
    static TrymigrateDatabase of(String jdbcUrl, String username, String password) {
        return new TrymigrateDatabase() {
            @Override
            public void prepare() {
            }

            @Override
            public String getJdbcUrl() {
                return jdbcUrl;
            }

            @Override
            public String getUsername() {
                return username;
            }

            @Override
            public String getPassword() {
                return password;
            }

            @Override
            public void dispose() {
            }
        };
    }

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
