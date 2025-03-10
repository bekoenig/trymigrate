package io.github.bekoenig.trymigrate.core.internal.bean;

import io.github.bekoenig.trymigrate.core.config.TrymigrateBeanProvider;
import io.github.bekoenig.trymigrate.core.config.TrymigratePlugin;
import org.junit.platform.commons.util.ReflectionUtils;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Optional;

public class PluginFactory {

    private PluginFactory() {
    }

    public static TrymigratePlugin instantiatePlugin(Class<? extends TrymigratePlugin> clazz, TrymigrateBeanProvider beanProvider) {
        TrymigratePlugin plugin = newInstance(clazz, beanProvider);
        if (plugin instanceof PluginServiceLoader) {
            return ((PluginServiceLoader) plugin).load();
        }

        return plugin;
    }

    private static TrymigratePlugin newInstance(Class<? extends TrymigratePlugin> clazz, TrymigrateBeanProvider beanProvider) {
        Optional<Constructor<?>> beanProviderConstructor = Arrays.stream(clazz.getConstructors())
                .filter(c -> c.getParameters().length == 1
                        && TrymigrateBeanProvider.class.isAssignableFrom(c.getParameters()[0].getType()))
                .findFirst();

        if (beanProviderConstructor.isPresent()) {
            return (TrymigratePlugin) ReflectionUtils.newInstance(beanProviderConstructor.get(), beanProvider);
        }

        return ReflectionUtils.newInstance(clazz);
    }

}
