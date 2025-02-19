package io.github.bekoenig.trymigrate.core.internal.bean;

import io.github.bekoenig.trymigrate.core.config.TrymigratePlugin;

public class BeanProviderSupport {

    private BeanProviderSupport() {
    }

    public static BeanProvider fromAnnotatedFields(Object instance, BeanHierarchy hierarchy) {
        return new BeanProvider(BeanDefinitions.fromAnnotatedFields(instance, hierarchy));
    }

    public static BeanProvider createHierarchy(Object testInstance, Class<? extends TrymigratePlugin> pluginClass) {
        BeanProvider beanProvider = fromAnnotatedFields(testInstance, BeanHierarchy.INSTANCE);

        TrymigratePlugin plugin = PluginFactory.instantiatePlugin(pluginClass, beanProvider);
        beanProvider = beanProvider.append(plugin, BeanHierarchy.PLUGIN);

        DefaultBeans defaultBeans = new DefaultBeans(beanProvider);
        beanProvider = beanProvider.append(defaultBeans, BeanHierarchy.DEFAULT);

        return beanProvider;
    }

}
