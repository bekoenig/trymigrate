package io.github.bekoenig.trymigrate.core.plugin.customize;

import org.testcontainers.containers.JdbcDatabaseContainer;

import java.util.function.Consumer;

public interface TrymigrateContainerConfigurer extends Consumer<JdbcDatabaseContainer<?>> {
}
