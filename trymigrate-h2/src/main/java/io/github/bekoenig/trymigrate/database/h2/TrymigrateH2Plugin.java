package io.github.bekoenig.trymigrate.database.h2;

import io.github.bekoenig.trymigrate.core.plugin.TrymigratePlugin;

/**
 * Marker interface for SPI plugins that should only participate in H2-focused discovery.
 */
public interface TrymigrateH2Plugin extends TrymigratePlugin {
}
