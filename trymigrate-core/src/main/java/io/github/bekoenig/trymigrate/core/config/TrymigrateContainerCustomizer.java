package io.github.bekoenig.trymigrate.core.config;

import org.testcontainers.containers.JdbcDatabaseContainer;

import java.util.function.Consumer;

public interface TrymigrateContainerCustomizer extends Consumer<JdbcDatabaseContainer<?>> {
}
