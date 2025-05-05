package io.github.bekoenig.trymigrate.core.internal.bean;

import io.github.bekoenig.trymigrate.core.plugin.TrymigrateBeanProvider;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

public record BeanProvider(List<BeanDefinition> beanDefinitions) implements TrymigrateBeanProvider {

    private <T> Stream<T> stream(Class<T> clazz) {
        return beanDefinitions.stream()
                .filter(x -> x.is(clazz))
                .map(x -> x.get(clazz))
                .filter(Objects::nonNull);
    }

    @Override
    public <T> List<T> all(Class<T> clazz) {
        return beanDefinitions.stream()
                .filter(x -> x.is(clazz) || x.isCollection(clazz))
                .flatMap(x -> x.getCollection(clazz).stream())
                .filter(Objects::nonNull)
                .toList();
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
            throw new IllegalStateException("Multiple beans for type " + clazz.getSimpleName());
        }

        return Optional.of(values.get(0));
    }

    @Override
    public <T> T one(Class<T> clazz) {
        return findOne(clazz)
                .orElseThrow(() -> new IllegalStateException("Missing bean for type " + clazz.getSimpleName()));
    }

    @Override
    public <T> Optional<T> findFirst(Class<T> clazz) {
        return all(clazz).stream().findFirst();
    }

    @Override
    public <T> T first(Class<T> clazz) {
        return stream(clazz)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Missing bean for type " + clazz.getSimpleName()));
    }

}
