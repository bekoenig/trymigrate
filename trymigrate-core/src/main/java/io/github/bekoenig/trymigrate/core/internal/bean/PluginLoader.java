package io.github.bekoenig.trymigrate.core.internal.bean;

import io.github.bekoenig.trymigrate.core.config.TrymigratePlugin;
import org.junit.platform.commons.util.ReflectionUtils;

import java.util.List;
import java.util.ServiceLoader;

public class PluginLoader {

    private PluginLoader() {
    }

    public static List<TrymigratePlugin> load(Class<? extends TrymigratePlugin> pluginClass) {
        ServiceLoader<TrymigratePlugin> serviceLoader = ServiceLoader.load(TrymigratePlugin.class);

        List<TrymigratePlugin> plugins = serviceLoader.stream()
                .filter(plugin -> pluginClass.isAssignableFrom(plugin.type()))
                .map(ServiceLoader.Provider::get)
                .toList();
        if (!plugins.isEmpty()) {
            return plugins;
        }

        if (pluginClass.isInterface()) {
            // allow no plugin on undefined service types and base interface
            if (pluginClass.equals(TrymigratePlugin.class)) {
                return List.of();
            }

            throw new IllegalStateException("No service type for interface " + pluginClass.getName() + " supplied");
        }

        // instantiate plugin using default constructor
        return List.of(ReflectionUtils.newInstance(pluginClass));
    }

}
