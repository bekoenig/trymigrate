package io.github.bekoenig.trymigrate.core.internal.plugin;

import io.github.bekoenig.trymigrate.core.plugin.TrymigrateBeanProvider;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

public record BeanProvider(List<BeanDefinition> beanDefinitions) implements TrymigrateBeanProvider {

    private <T> Stream<T> stream(Class<T> clazz) {
        return beanDefinitions.stream()
                .filter(x -> x.isCompatible(clazz))
                .flatMap(x -> x.get().stream())
                .map(clazz::cast)
                .filter(Objects::nonNull);
    }

    @Override
    public <T> List<T> all(Class<T> clazz) {
        return stream(clazz).toList();
    }

    @Override
    public <T> Optional<T> findOne(Class<T> clazz) {
        List<T> values = stream(clazz)
                .limit(2)
                .toList();

        if (values.isEmpty()) {
            return Optional.empty();
        }

        if (values.size() > 1) {
            throw new IllegalStateException("Multiple beans for type " + clazz.getName());
        }

        return Optional.of(values.get(0));
    }

    @Override
    public <T> T one(Class<T> clazz) {
        return findOne(clazz)
                .orElseThrow(() -> new IllegalStateException("Missing bean for type " + clazz.getName()));
    }

    @Override
    public <T> Optional<T> findFirst(Class<T> clazz) {
        return stream(clazz).findFirst();
    }

    @Override
    public <T> T first(Class<T> clazz) {
        return stream(clazz)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Missing bean for type " + clazz.getName()));
    }

}
