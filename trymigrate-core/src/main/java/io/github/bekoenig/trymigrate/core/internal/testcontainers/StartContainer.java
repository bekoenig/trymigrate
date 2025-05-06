package io.github.bekoenig.trymigrate.core.internal.testcontainers;

import io.github.bekoenig.trymigrate.core.plugin.customize.TrymigrateContainerConfigurer;
import org.testcontainers.containers.JdbcDatabaseContainer;

/**
 * Customizer for start container.
 */
public class StartContainer implements TrymigrateContainerConfigurer {

    @Override
    public void accept(JdbcDatabaseContainer<?> jdbcDatabaseContainer) {
        jdbcDatabaseContainer.start();
    }

}
