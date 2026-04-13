package io.github.bekoenig.trymigrate.core.internal.database;

import io.github.bekoenig.trymigrate.core.plugin.customize.TrymigrateDatabase;

import java.util.Optional;
import java.util.function.Function;

/**
 * Decorator for {@link TrymigrateDatabase} that adds support for property-based overrides.
 * <p>
 * Supported properties:
 * <ul>
 *     <li>{@value #PROPERTY_NAME_LIFECYCLE_ENABLED}: If false, {@link #prepare()} and {@link #dispose()}
 *     of the delegate are not called. Defaults to true.</li>
 *     <li>{@value #PROPERTY_NAME_URL}: Overrides the JDBC URL.</li>
 *     <li>{@value #PROPERTY_NAME_USER}: Overrides the database username.</li>
 *     <li>{@value #PROPERTY_NAME_PASSWORD}: Overrides the database password.</li>
 * </ul>
 */
public class DatabaseDecorator implements TrymigrateDatabase {

    public static final String PROPERTY_NAME_LIFECYCLE_ENABLED = "trymigrate.database.lifecycle.enabled";
    public static final String PROPERTY_NAME_URL = "trymigrate.database.url";
    public static final String PROPERTY_NAME_USER = "trymigrate.database.user";
    public static final String PROPERTY_NAME_PASSWORD = "trymigrate.database.password";

    private final TrymigrateDatabase delegate;
    private final String url;
    private final String user;
    private final String password;
    private final boolean lifecycleEnabled;

    /**
     * @param delegate the original database plugin (may be null)
     */
    public DatabaseDecorator(TrymigrateDatabase delegate) {
        this.delegate = delegate;
        this.url = System.getProperty(PROPERTY_NAME_URL);
        this.user = System.getProperty(PROPERTY_NAME_USER);
        this.password = System.getProperty(PROPERTY_NAME_PASSWORD);
        this.lifecycleEnabled = Boolean.parseBoolean(System.getProperty(PROPERTY_NAME_LIFECYCLE_ENABLED, "true"));
    }

    /**
     * Checks if a database is defined either by a delegate or by properties.
     *
     * @return true if a delegate exists or at least one database property is set.
     */
    public boolean isDefined() {
        return delegate != null || url != null || user != null || password != null;
    }

    @Override
    public <T> Optional<T> unwrap(Class<T> type) {
        if (delegate != null) {
            Optional<T> unwrapped = delegate.unwrap(type);
            if (unwrapped.isPresent()) {
                return unwrapped;
            }
        }
        return TrymigrateDatabase.super.unwrap(type);
    }

    private String getValue(String propertyValue, Function<TrymigrateDatabase, String> getter) {
        if (propertyValue != null) {
            return propertyValue;
        }
        return delegate != null ? getter.apply(delegate) : null;
    }

    @Override
    public void prepare() {
        if (lifecycleEnabled && delegate != null) {
            delegate.prepare();
        }
    }

    @Override
    public String getJdbcUrl() {
        return getValue(url, TrymigrateDatabase::getJdbcUrl);
    }

    @Override
    public String getUsername() {
        return getValue(user, TrymigrateDatabase::getUsername);
    }

    @Override
    public String getPassword() {
        return getValue(password, TrymigrateDatabase::getPassword);
    }

    @Override
    public void dispose() {
        if (lifecycleEnabled && delegate != null) {
            delegate.dispose();
        }
    }

}
