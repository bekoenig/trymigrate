package io.github.bekoenig.trymigrate.core.internal.plugin;

import io.github.bekoenig.trymigrate.core.plugin.TrymigratePlugin;
import io.github.bekoenig.trymigrate.core.plugin.TrymigratePluginProvider;

import java.util.List;
import java.util.ServiceLoader;
import java.util.stream.Stream;

/**
 * Advanced discovery for {@link TrymigratePluginProvider} using {@link ServiceLoader}. Supports hierarchical ranking
 * for {@link TrymigratePlugin}, explicitly excludes and skipping of unloadable classes.
 * <p>
 * Note: Some implementations could be moved to {@link TrymigratePluginProvider} for a more flexible way of discovery.
 */
public class PluginDiscovery {

    @SuppressWarnings("rawtypes")
    private final ServiceLoader<TrymigratePluginProvider> serviceLoader;

    public PluginDiscovery() {
        serviceLoader = ServiceLoader.load(TrymigratePluginProvider.class);
    }

    public List<GenericPluginProvider> discover(Class<? extends TrymigratePlugin> interfaceType,
                                                Class<? extends TrymigratePlugin>[] excludedTypes) {
        if (!interfaceType.isInterface()) {
            throw new IllegalArgumentException("Only interfaces are supported.");
        }

        return serviceLoader
                .stream()
                .map(ServiceLoader.Provider::get)
                .filter(PluginDiscovery::hasLoadableType)
                .filter(p -> hasCommonSuperinterface(p.forType(), interfaceType))
                .map(PluginDiscovery::toGenericType)
                .filter(p -> !ofType(p.forType(), excludedTypes))
                .map(p -> new GenericPluginProvider(p, calculateRank(p.forType())))
                .toList();
    }

    @SuppressWarnings("unchecked")
    private static TrymigratePluginProvider<TrymigratePlugin> toGenericType(TrymigratePluginProvider<?> provider) {
        return (TrymigratePluginProvider<TrymigratePlugin>) provider;
    }

    protected static boolean hasLoadableType(TrymigratePluginProvider<TrymigratePlugin> provider) {
        try {
            provider.forType();
            return true;
        } catch (NoClassDefFoundError e) {
            return false;
        }
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
    protected static int calculateRank(Class<? extends TrymigratePlugin> plugin) {
        int incrementer = plugin.isInterface() ? 1 : 0;
        return Stream.of(plugin.getInterfaces())
                .filter(TrymigratePlugin.class::isAssignableFrom)
                .map(i -> (Class<? extends TrymigratePlugin>) i)
                .mapToInt(i -> incrementer + calculateRank(i))
                .max().orElse(0);
    }

}
