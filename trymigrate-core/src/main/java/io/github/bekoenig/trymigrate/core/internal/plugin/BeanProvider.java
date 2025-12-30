package io.github.bekoenig.trymigrate.core.internal.plugin;

import io.github.bekoenig.trymigrate.core.plugin.TrymigrateBean;
import io.github.bekoenig.trymigrate.core.plugin.TrymigratePlugin;

import java.util.*;
import java.util.stream.Stream;

/**
 * Facade to lookup registered {@link TrymigrateBean} in {@link TrymigratePlugin} and test instance.
 *
 * @param beanDefinitions list of discovered {@link BeanDefinition}
 */
public record BeanProvider(List<BeanDefinition> beanDefinitions) {

    private <T> Stream<T> stream(Class<T> clazz) {
        return beanDefinitions.stream()
                .filter(x -> x.isCompatible(clazz))
                .flatMap(x -> x.get().stream())
                .map(clazz::cast);
    }

    /**
     * Gets all compatible beans for class from highest to lowest order.
     *
     * @param clazz class
     * @return list of beans
     * @param <T> type of bean
     */
    public <T> List<T> all(Class<T> clazz) {
        return stream(clazz).toList();
    }

    /**
     * Gets all compatible beans for class from lowest to highest order.
     *
     * @param clazz class
     * @return list of beans
     * @param <T> type of bean
     */
    public <T> List<T> allReservedOrder(Class<T> clazz) {
        List<T> values = new ArrayList<>(all(clazz));
        Collections.reverse(values);
        return values;
    }

    /**
     * Gets one or no compatible bean for class.
     *
     * @throws IllegalStateException on multiple beans
     * @param clazz class
     * @return one or no bean
     * @param <T> type of bean
     */
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

    /**
     * Gets exactly one compatible bean for class.
     *
     * @throws IllegalStateException on missing or multiple beans
     * @param clazz class
     * @return one bean
     * @param <T> type of bean
     */
    public <T> T one(Class<T> clazz) {
        return findOne(clazz)
                .orElseThrow(() -> new IllegalStateException("Missing bean for type " + clazz.getName()));
    }

    /**
     * Gets one or no compatible bean for class. On multiple beans, the highest order will be used.
     *
     * @param clazz class
     * @return one or no bean
     * @param <T> type of bean
     */
    public <T> Optional<T> findFirst(Class<T> clazz) {
        return stream(clazz).findFirst();
    }

    /**
     * Gets exactly one compatible bean for class. On multiple beans, the highest order will be used.
     *
     * @throws IllegalStateException on no bean
     * @param clazz class
     * @return one bean
     * @param <T> type of bean
     */
    public <T> T first(Class<T> clazz) {
        return findFirst(clazz)
                .orElseThrow(() -> new IllegalStateException("Missing bean for type " + clazz.getName()));
    }

}
