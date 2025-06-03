package io.github.bekoenig.trymigrate.core.internal.plugin;

import io.github.bekoenig.trymigrate.core.plugin.TrymigratePlugin;

import java.util.List;
import java.util.ServiceLoader;
import java.util.stream.Stream;

public class PluginDiscovery {

    private final ServiceLoader<TrymigratePlugin> serviceLoader;

    public PluginDiscovery() {
        this.serviceLoader = ServiceLoader.load(TrymigratePlugin.class);
    }

    public List<PluginProvider> discover(Class<? extends TrymigratePlugin> interfaceType,
                                         Class<? extends TrymigratePlugin>[] excludedTypes) {
        if (!interfaceType.isInterface()) {
            throw new IllegalArgumentException("Only interfaces are supported.");
        }

        return serviceLoader
                .stream()
                .filter(p -> hasCommonSuperinterface(p.type(), interfaceType))
                .filter(p -> !ofType(p.type(), excludedTypes))
                .map(p -> new PluginProvider(p, countIntermediateInterfaces(p.type())))
                .toList();
    }

    protected static boolean hasCommonSuperinterface(Class<?> pluginType, Class<?> interfaceType) {
        // return true if any interface of the plugin type is a parent or a child of the specified interface type
        return Stream.of(pluginType.getInterfaces())
                .anyMatch(p -> p.isAssignableFrom(interfaceType) || interfaceType.isAssignableFrom(p));
    }

    protected static boolean ofType(Class<? extends TrymigratePlugin> pluginType,
                                    Class<? extends TrymigratePlugin>[] types) {
        // return true if the plugin type matches any specific type (interface or class)
        return Stream.of(types).anyMatch(e -> e.isAssignableFrom(pluginType));
    }

    @SuppressWarnings("unchecked")
    protected static int countIntermediateInterfaces(Class<? extends TrymigratePlugin> plugin) {
        int incrementer = plugin.isInterface() ? 1 : 0;
        return Stream.of(plugin.getInterfaces())
                .filter(TrymigratePlugin.class::isAssignableFrom)
                .map(i -> (Class<? extends TrymigratePlugin>) i)
                .mapToInt(i -> incrementer + countIntermediateInterfaces(i))
                .max().orElse(0);
    }

}
