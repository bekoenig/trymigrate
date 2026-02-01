package io.github.bekoenig.trymigrate.core.plugin.customize;

/**
 * Integrates a test database.
 */
public interface TrymigrateDatabase {

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
