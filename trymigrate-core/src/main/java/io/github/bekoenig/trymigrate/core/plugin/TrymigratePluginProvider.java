package io.github.bekoenig.trymigrate.core.plugin;

import java.lang.reflect.ParameterizedType;

/**
 * Provider loaded by {@link java.util.ServiceLoader} to supply instances of {@link TrymigratePlugin}.
 * <p>
 * @param <T> provided type
 */
public interface TrymigratePluginProvider<T extends TrymigratePlugin> {

    /**
     * Returns the provided type of this plugin.
     * <p>
     * Allows support for skipping implementations of sub-interfaces of {@link TrymigratePlugin} which are not in
     * classpath on plugin discovery.
     *
     * @return provided type
     */
    default Class<T> forType() {
        // reflect generic type for provider implementations with single interface
        if (getClass().getGenericInterfaces().length == 1
                && getClass().getGenericInterfaces()[0] instanceof ParameterizedType parameterizedType
                && parameterizedType.getActualTypeArguments().length == 1
                && parameterizedType.getActualTypeArguments()[0] instanceof Class<?> actualTypeArguments
                && TrymigratePlugin.class.isAssignableFrom(actualTypeArguments)) {
            return (Class<T>) actualTypeArguments;
        }

        throw new UnsupportedOperationException("Failed to reflect provided type using default implementation.");
    }

    /**
     * Provides a new instance of {@link TrymigratePlugin}.
     * <p>
     * The {@link TrymigrateBeanProvider} populates all beans from plugins with higher hierarchy and the current test
     * instance to this plugin. Will be invoked after plugin initialization in test instance post-processing. All fields
     * annotated with {@link TrymigrateBean} of this plugin are collected after method call.
     *
     * @param beanProvider {@link TrymigrateBeanProvider} with beans from plugin with higher priority
     *                                                   or the current test instance
     * @return new instance of {@link TrymigratePlugin}
     */
    T provide(TrymigrateBeanProvider beanProvider);

}
