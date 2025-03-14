package io.github.bekoenig.trymigrate.core.internal.bean;

import io.github.bekoenig.trymigrate.core.config.TrymigratePlugin;
import org.junit.platform.commons.util.ReflectionUtils;

import java.util.List;
import java.util.ServiceLoader;

public class PluginDiscovery {

    private final ServiceLoader<TrymigratePlugin> serviceLoader;

    public PluginDiscovery() {
        this.serviceLoader = ServiceLoader.load(TrymigratePlugin.class);
    }

    public List<PluginProvider> discover(List<Class<? extends TrymigratePlugin>> roots) {
        return roots.stream()
                .map(this::discover)
                .flatMap(List::stream)
                .distinct()
                .toList();
    }

    private List<PluginProvider> discover(Class<? extends TrymigratePlugin> root) {
        List<PluginProvider> plugins = serviceLoader.stream()
                .filter(plugin -> root.isAssignableFrom(plugin.type()))
                .map(provider -> new PluginProvider(provider.type(), provider))
                .toList();

        // use spi only on any match
        if (!plugins.isEmpty()) {
            return plugins;
        }

        if (root.isInterface()) {
            // allow no plugin on undefined service types and base interface
            if (root.equals(TrymigratePlugin.class)) {
                return List.of();
            }

            throw new IllegalStateException("Failed to discover plugins for " + root.getName() + ". " +
                    "Register plugin implementations using service provider interface or discover using class.");
        }

        // instantiate plugin using default constructor
        return List.of(new PluginProvider(root, () -> ReflectionUtils.newInstance(root)));
    }

}
