package io.github.bekoenig.trymigrate.core.lint.config;

import java.util.function.Consumer;

/**
 * Configurer for {@link TrymigrateLinterConfiguration}. The entire entry point to add fluent linter configurations.
 *
 * @see TrymigrateLinterConfiguration
 */
public interface TrymigrateLintersConfigurer extends Consumer<TrymigrateLintersConfiguration> {
}
