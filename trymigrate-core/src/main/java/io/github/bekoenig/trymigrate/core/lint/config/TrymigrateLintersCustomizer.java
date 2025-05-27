package io.github.bekoenig.trymigrate.core.lint.config;

import java.util.function.Consumer;

/**
 * Customizer for {@link TrymigrateLinterConfiguration}. The entire entry point to add fluent linter configurations.
 *
 * @see TrymigrateLinterConfiguration
 */
public interface TrymigrateLintersCustomizer extends Consumer<TrymigrateLintersConfiguration> {
}
