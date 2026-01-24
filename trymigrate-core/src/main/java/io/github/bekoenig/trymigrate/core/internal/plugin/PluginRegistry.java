package io.github.bekoenig.trymigrate.core.internal.plugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public record PluginRegistry(List<PluginProvider> pluginProviders) {

    private <T> Stream<T> stream(Class<T> clazz) {
        if (!PluginTypesValidator.isSupportedType(clazz)) {
            throw new IllegalArgumentException("Unsupported plugin type " + clazz.getName());
        }

        return pluginProviders.stream()
                .filter(x -> x.isInstanceOf(clazz))
                .map(PluginProvider::provide)
                .map(clazz::cast);
    }

    public <T> List<T> all(Class<T> clazz) {
        return stream(clazz).toList();
    }

    public <T> List<T> allReservedOrder(Class<T> clazz) {
        List<T> values = new ArrayList<>(all(clazz));
        Collections.reverse(values);
        return values;
    }

    public <T> Optional<T> findOne(Class<T> clazz) {
        List<T> values = stream(clazz)
                .limit(2)
                .toList();

        if (values.isEmpty()) {
            return Optional.empty();
        }

        if (values.size() > 1) {
            throw new IllegalStateException("Multiple plugins for type " + clazz.getName());
        }

        return Optional.of(values.get(0));
    }

}
