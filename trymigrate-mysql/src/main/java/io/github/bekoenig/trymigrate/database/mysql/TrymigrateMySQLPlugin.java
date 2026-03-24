package io.github.bekoenig.trymigrate.database.mysql;

import io.github.bekoenig.trymigrate.core.plugin.TrymigratePlugin;

/**
 * Marker interface for SPI plugins that should only participate in MySQL-focused discovery.
 */
public interface TrymigrateMySQLPlugin extends TrymigratePlugin {
}
