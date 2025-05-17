package io.github.bekoenig.trymigrate.core.internal.container;

import org.testcontainers.containers.JdbcDatabaseContainer;

import java.util.function.Consumer;

/**
 * Customizer for start container.
 */
public class ContainerStarter implements Consumer<JdbcDatabaseContainer<?>> {

    @Override
    public void accept(JdbcDatabaseContainer<?> jdbcDatabaseContainer) {
        jdbcDatabaseContainer.start();
    }

}
