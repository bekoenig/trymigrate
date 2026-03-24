package io.github.bekoenig.trymigrate.database.postgresql;

import io.github.bekoenig.trymigrate.core.plugin.TrymigratePlugin;

/**
 * Marker interface for SPI plugins that should only participate in PostgreSQL-focused discovery.
 */
public interface TrymigratePostgreSQLPlugin extends TrymigratePlugin {
}
