package io.github.bekoenig.trymigrate.core.plugin;

import io.github.bekoenig.trymigrate.core.Trymigrate;

/**
 * Marker interface for loadable plugins by {@link java.util.ServiceLoader}. Each plugin implementation supplies one
 * or more customizations by fields annotated with {@link TrymigrateBean}.
 * <p>
 * The priority of a plugin is the forced order for beans. It is specified by the number of interface extensions
 * above {@link TrymigratePlugin} and the directly implemented interface of the plugin. All core plugins, which
 * implement {@link TrymigratePlugin}, have the lowest priority. Database-specific plugins should implement an interface
 * extension located in the database module. Context and database-specific plugins and more detailed abstractions are
 * also supported.
 * <p>
 * The current test instance has always the highest priority.
 *
 * @see Trymigrate#plugin()
 */
public interface TrymigratePlugin {

    /**
     * Populates all beans from plugins with higher hierarchy and the current test instance to this plugin. Will be
     * invoked after plugin initialisation in test instance post-processing. All fields annotated with
     * {@link TrymigrateBean} of this plugin are collected after method call.
     *
     * @param beanProvider {@link TrymigrateBeanProvider} with beans from plugin with higher priority
     *                                                   or the current test instance
     */
    default void populate(TrymigrateBeanProvider beanProvider) {
    }

}
