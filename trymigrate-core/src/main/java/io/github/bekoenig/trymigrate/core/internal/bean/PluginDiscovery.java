package io.github.bekoenig.trymigrate.core.internal.bean;

import io.github.bekoenig.trymigrate.core.plugin.TrymigratePlugin;

import java.util.*;
import java.util.stream.Stream;

public class PluginDiscovery {

    private final ServiceLoader<TrymigratePlugin> serviceLoader;

    public PluginDiscovery() {
        this.serviceLoader = ServiceLoader.load(TrymigratePlugin.class);
    }

    public List<PluginProvider> discover(Class<? extends TrymigratePlugin> branch) {
        if (!branch.isInterface()) {
            throw new IllegalArgumentException("Only interfaces are supported.");
        }

        return serviceLoader
                .stream()
                .filter(p -> hasCompatibleSuperinterface(p.type(), branch))
                .map(p -> new PluginProvider(p, countIntermediateInterfaces(p.type())))
                .toList();
    }

    protected static boolean hasCompatibleSuperinterface(Class<?> pluginType, Class<?> interfaceType) {
        return Stream.of(pluginType.getInterfaces())
                .anyMatch(p -> p.isAssignableFrom(interfaceType) || interfaceType.isAssignableFrom(p));
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
