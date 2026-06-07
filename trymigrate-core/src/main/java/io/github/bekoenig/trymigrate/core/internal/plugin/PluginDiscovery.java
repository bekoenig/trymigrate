package io.github.bekoenig.trymigrate.core.internal.plugin;

import io.github.bekoenig.trymigrate.core.plugin.TrymigratePlugin;

import java.util.List;
import java.util.ServiceLoader;
import java.util.stream.Stream;

/**
 * Advanced discovery for {@link TrymigratePlugin} using {@link ServiceLoader}. Supports hierarchical ranking
 * for {@link TrymigratePlugin} and explicit excludes.
 */
public class PluginDiscovery {

    private final ServiceLoader<TrymigratePlugin> serviceLoader;

    public PluginDiscovery() {
        serviceLoader = ServiceLoader.load(TrymigratePlugin.class);
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
                .map(p -> new PluginProvider(p.type(), p::get, calculateRank(p.type())))
                .sorted()
                .toList();
    }

    protected static boolean hasCommonSuperinterface(Class<?> pluginType, Class<?> interfaceType) {
        // return true if any interface of the plugin type is a parent or a child of the specified interface type
        return allInterfaces(pluginType)
                .anyMatch(p -> p.isAssignableFrom(interfaceType) || interfaceType.isAssignableFrom(p));
    }

    protected static boolean ofType(Class<? extends TrymigratePlugin> pluginType,
                                    Class<? extends TrymigratePlugin>[] types) {
        // return true if the plugin type matches any specific type (interface or class)
        return Stream.of(types).anyMatch(e -> e.isAssignableFrom(pluginType));
    }

    @SuppressWarnings("unchecked")
    protected static int calculateRank(Class<? extends TrymigratePlugin> plugin) {
        Stream<Class<?>> interfaces = plugin.isInterface()
                ? Stream.of(plugin.getInterfaces())
                : allInterfaces(plugin);

        return interfaces
                .filter(TrymigratePlugin.class::isAssignableFrom)
                .map(i -> (Class<? extends TrymigratePlugin>) i)
                .mapToInt(i -> (plugin.isInterface() ? 1 : 0) + calculateRank(i))
                .max().orElse(0);
    }

    private static Stream<Class<?>> allInterfaces(Class<?> type) {
        if (type == null) {
            return Stream.empty();
        }

        return Stream.concat(
                        Stream.of(type.getInterfaces())
                                .flatMap(i -> Stream.concat(Stream.of(i), allInterfaces(i))),
                        allInterfaces(type.getSuperclass()))
                .distinct();
    }

}
