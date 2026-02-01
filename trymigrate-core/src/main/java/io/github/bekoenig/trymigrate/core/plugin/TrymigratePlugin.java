package io.github.bekoenig.trymigrate.core.plugin;

import io.github.bekoenig.trymigrate.core.plugin.customize.*;
import org.flywaydb.core.api.callback.Callback;
import org.flywaydb.core.api.migration.JavaMigration;

import java.util.List;

/**
 * Marker interface for plugin all {@link TrymigratePlugin#SUPPORTED_TYPES} using
 * {@link java.util.ServiceLoader}.
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

    /**
     * Supported plugin supertypes.
     */
    List<Class<?>> SUPPORTED_TYPES = List.of(
            TrymigrateCatalogCustomizer.class,
            TrymigrateDataLoader.class,
            TrymigrateFlywayCustomizer.class,
            TrymigrateLintersConfigurer.class,
            TrymigrateLintOptionsCustomizer.class,
            TrymigrateLintsReporter.class,
            TrymigrateDatabase.class,
            Callback.class,
            JavaMigration.class
    );

}
