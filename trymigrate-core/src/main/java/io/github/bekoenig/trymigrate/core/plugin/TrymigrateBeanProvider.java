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
     * Gets all compatible beans for class ordered.
     *
     * @param clazz class
     * @return list of beans
     * @param <T> type of bean
     */
    <T> List<T> all(Class<T> clazz);

    /**
     * Gets exactly one compatible bean for class.
     *
     * @throws IllegalStateException on missing or multiple beans
     * @param clazz class
     * @return one bean
     * @param <T> type of bean
     */
    <T> T one(Class<T> clazz);

    /**
     * Gets one or no compatible bean for class.
     *
     * @throws IllegalStateException on multiple beans
     * @param clazz class
     * @return one or no bean
     * @param <T> type of bean
     */
    <T> Optional<T> findOne(Class<T> clazz);

    /**
     * Gets exactly one compatible bean for class. On multiple beans, the highest order will be used.
     *
     * @throws IllegalStateException on no bean
     * @param clazz class
     * @return one bean
     * @param <T> type of bean
     */
    <T> T first(Class<T> clazz);

    /**
     * Gets one or no compatible bean for class. On multiple beans, the highest order will be used.
     *
     * @param clazz class
     * @return one or no bean
     * @param <T> type of bean
     */
    <T> Optional<T> findFirst(Class<T> clazz);
}
