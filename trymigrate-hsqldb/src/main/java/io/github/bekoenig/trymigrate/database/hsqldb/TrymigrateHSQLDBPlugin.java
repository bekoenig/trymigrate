package io.github.bekoenig.trymigrate.database.hsqldb;

import io.github.bekoenig.trymigrate.core.plugin.TrymigratePlugin;

/**
 * Marker interface for SPI plugins that should only participate in HSQLDB-focused discovery.
 */
public interface TrymigrateHSQLDBPlugin extends TrymigratePlugin {
}
