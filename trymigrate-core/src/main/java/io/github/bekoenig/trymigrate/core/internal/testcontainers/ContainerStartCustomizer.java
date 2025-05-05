package io.github.bekoenig.trymigrate.core.internal.testcontainers;

import io.github.bekoenig.trymigrate.core.config.TrymigrateContainerCustomizer;
import org.testcontainers.containers.JdbcDatabaseContainer;

/**
 * Customizer for start container.
 */
public class ContainerStartCustomizer implements TrymigrateContainerCustomizer {

    @Override
    public void accept(JdbcDatabaseContainer<?> jdbcDatabaseContainer) {
        jdbcDatabaseContainer.start();
    }

}
