package io.github.bekoenig.trymigrate.core.internal.bean;

import io.github.bekoenig.trymigrate.core.config.TrymigratePlugin;

public class BeanProviderBuilder {

    private final BeanProvider testInstanceBeanProvider;
    private BeanProvider fluentBeanProvider;

    public BeanProviderBuilder(Object testInstance) {
        fluentBeanProvider = new BeanProvider(BeanDefinitions.fromAnnotatedFields(testInstance, BeanHierarchy.INSTANCE));
        testInstanceBeanProvider = fluentBeanProvider;
    }

    public BeanProviderBuilder loadPlugins(Class<? extends TrymigratePlugin> pluginClass) {
        for (TrymigratePlugin plugin : PluginLoader.load(pluginClass)) {
            plugin.populate(testInstanceBeanProvider);
            fluentBeanProvider = fluentBeanProvider.append(plugin, BeanHierarchy.PLUGIN);
        }
        return this;
    }

    public BeanProvider build() {
        return fluentBeanProvider.append(new DefaultBeans(fluentBeanProvider), BeanHierarchy.DEFAULT);
    }

}
