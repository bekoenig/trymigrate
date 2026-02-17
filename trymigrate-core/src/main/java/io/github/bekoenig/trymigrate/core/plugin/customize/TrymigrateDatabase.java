package io.github.bekoenig.trymigrate.core.plugin.customize;

import java.util.Optional;

/**
 * Integrates a test database.
 */
public interface TrymigrateDatabase {

    /**
     * Provides access to the underlying instance if it matches the given type.
     *
     * @param <T>  the target type
     * @param type the class of the target type
     * @return an {@link Optional} with the instance, or empty if type doesn't match
     */
    default <T> Optional<T> unwrap(Class<T> type) {
        if (type.isInstance(this)) {
            return Optional.of(type.cast(this));
        }
        return Optional.empty();
    }

    /**
     * Prepares the database for test instance.
     */
    void prepare();

    /**
     * @return jdbc-url
     */
    String getJdbcUrl();

    /**
     * @return username
     */
    String getUsername();

    /**
     * @return password
     */
    String getPassword();

    /**
     * Disposes the database after test instance.
     */
    void dispose();

}
