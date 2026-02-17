package io.github.bekoenig.trymigrate.core.internal.container;

import io.github.bekoenig.trymigrate.core.plugin.customize.TrymigrateDatabase;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.lifecycle.Startable;

import java.util.Optional;

/**
 * Adapter to use a {@link JdbcDatabaseContainer} as {@link TrymigrateDatabase}.
 */
public class JdbcDatabaseContainerAdapter implements TrymigrateDatabase {

    private final JdbcDatabaseContainer<?> container;
    private final boolean shared;

    public JdbcDatabaseContainerAdapter(JdbcDatabaseContainer<?> container, boolean shared) {
        this.container = container;
        this.shared = shared;
    }

    @Override
    public <T> Optional<T> unwrap(Class<T> type) {
        if (type.isInstance(container)) {
            return Optional.of(type.cast(container));
        }
        return TrymigrateDatabase.super.unwrap(type);
    }

    @Override
    public void prepare() {
        new StaticPortBinding().andThen(Startable::start).accept(container);
    }

    @Override
    public String getJdbcUrl() {
        return container.getJdbcUrl();
    }

    @Override
    public String getUsername() {
        return container.getUsername();
    }

    @Override
    public String getPassword() {
        return container.getPassword();
    }

    @Override
    public void dispose() {
        if (!shared) {
            container.stop();
        }
    }
}
