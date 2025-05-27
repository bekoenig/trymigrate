package io.github.bekoenig.trymigrate.core.plugin;

import java.util.List;
import java.util.Optional;

/**
 * Facade to lookup registered {@link TrymigrateBean} in {@link TrymigratePlugin} and test instance.
 *
 * @see TrymigratePlugin#populate(TrymigrateBeanProvider)
 */
public interface TrymigrateBeanProvider {

    /**
     * Gets all beans for class ordered.
     *
     * @param clazz class
     * @return list of beans
     * @param <T> type of bean
     */
    <T> List<T> all(Class<T> clazz);

    <T> T one(Class<T> clazz);

    <T> Optional<T> findOne(Class<T> clazz);

    <T> T first(Class<T> clazz);

    <T> Optional<T> findFirst(Class<T> clazz);
}
