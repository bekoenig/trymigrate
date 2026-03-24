package io.github.bekoenig.trymigrate.database.sqlserver;

import io.github.bekoenig.trymigrate.core.plugin.TrymigratePlugin;

/**
 * Marker interface for SPI plugins that should only participate in SQL Server-focused discovery.
 */
public interface TrymigrateSQLServerPlugin extends TrymigratePlugin {
}
