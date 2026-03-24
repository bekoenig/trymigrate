package io.github.bekoenig.trymigrate.database.mariadb;

import io.github.bekoenig.trymigrate.core.plugin.TrymigratePlugin;

/**
 * Marker interface for SPI plugins that should only participate in MariaDB-focused discovery.
 */
public interface TrymigrateMariaDBPlugin extends TrymigratePlugin {
}
