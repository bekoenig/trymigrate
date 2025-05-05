package io.github.bekoenig.trymigrate.core.internal.testcontainers;

import io.github.bekoenig.trymigrate.core.plugin.customize.TrymigrateContainerCustomizer;
import org.testcontainers.containers.JdbcDatabaseContainer;

/**
 * Customizer for start container.
 */
public class StartContainer implements TrymigrateContainerCustomizer {

    @Override
    public void accept(JdbcDatabaseContainer<?> jdbcDatabaseContainer) {
        jdbcDatabaseContainer.start();
    }

}
