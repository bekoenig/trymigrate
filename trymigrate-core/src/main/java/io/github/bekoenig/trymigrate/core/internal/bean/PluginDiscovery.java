package io.github.bekoenig.trymigrate.core.internal.bean;

import io.github.bekoenig.trymigrate.core.config.TrymigratePlugin;
import org.junit.platform.commons.util.ReflectionUtils;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PluginDiscovery {

    private final ServiceLoader<TrymigratePlugin> serviceLoader;

    public PluginDiscovery() {
        this.serviceLoader = ServiceLoader.load(TrymigratePlugin.class);
    }

    public Set<PluginProvider> discover(List<Class<? extends TrymigratePlugin>> pluginTypes) {
        return pluginTypes.stream()
                .map(this::discover)
                .flatMap(Set::stream)
                .collect(Collectors.toSet());
    }

    private Set<PluginProvider> discover(Class<? extends TrymigratePlugin> pluginType) {
        Stream<ServiceLoader.Provider<TrymigratePlugin>> providerStream = serviceLoader.stream();
        if (!pluginType.equals(TrymigratePlugin.class)) {
            // all implicit referenced plugin interfaces by current plugin type
            Set<Class<? extends TrymigratePlugin>> allPluginInterfaces = allPluginInterfaces(pluginType);
            providerStream = providerStream.filter(pluginProvider ->
                    isReferenced(pluginProvider, allPluginInterfaces));
        }


        // lockup all spi registered implementations which are implicit referenced
        Set<PluginProvider> providers = providerStream
                .map(provider -> new PluginProvider(provider.type(), provider))
                .collect(Collectors.toSet());

        // add provider for non spi registered implementation
        if (!pluginType.isInterface() && providers.stream().noneMatch(x -> x.type().equals(pluginType))) {
            providers.add(new PluginProvider(pluginType, () -> ReflectionUtils.newInstance(pluginType)));
        }

        return providers;
    }

    private Set<Class<? extends TrymigratePlugin>> allPluginInterfaces(Class<? extends TrymigratePlugin> clazz) {
        Set<Class<? extends TrymigratePlugin>> interfaces = new HashSet<>();
        if (clazz.isInterface()) {
            interfaces.add(clazz);
        }
        interfaces.addAll(Stream.of(clazz.getInterfaces())
                .filter(TrymigratePlugin.class::isAssignableFrom)
                .map(this::asPluginClass)
                .map(this::allPluginInterfaces)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet()));
        return interfaces;
    }

    @SuppressWarnings("unchecked")
    private Class<? extends TrymigratePlugin> asPluginClass(Class<?> x) {
        return (Class<? extends TrymigratePlugin>) x;
    }

    private boolean isReferenced(
            ServiceLoader.Provider<? extends TrymigratePlugin> pluginProvider,
            Set<Class<? extends TrymigratePlugin>> allPluginInterfaces) {
        Set<Class<?>> pluginInterfaces = Stream.of(pluginProvider.type().getInterfaces())
                .filter(TrymigratePlugin.class::isAssignableFrom)
                .collect(Collectors.toSet());
        return allPluginInterfaces.stream().anyMatch(pluginInterfaces::contains);
    }

}
