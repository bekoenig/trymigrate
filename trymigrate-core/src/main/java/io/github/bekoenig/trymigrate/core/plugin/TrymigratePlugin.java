package io.github.bekoenig.trymigrate.core.plugin;

/**
 * Marker interface for plugins provided by {@link java.util.ServiceLoader}.
 * <p>
 * The priority of a plugin is specified by the number of interface extensions above {@link TrymigratePlugin} and the
 * directly implemented interface of the plugin. All core plugins, which implement {@link TrymigratePlugin}, have the
 * lowest priority. Database-specific plugins should implement an interface extension located in the database module.
 * Context and database-specific plugins and more detailed abstractions are also supported.
 * <p>
 * Plugins registered by {@link TrymigrateRegisterPlugin} have always the highest priority.
 *
 * @see TrymigrateDiscoverPlugins
 */
public interface TrymigratePlugin {
}
