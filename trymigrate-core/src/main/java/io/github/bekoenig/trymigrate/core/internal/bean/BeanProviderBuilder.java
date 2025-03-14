package io.github.bekoenig.trymigrate.core.internal.bean;

import io.github.bekoenig.trymigrate.core.config.TrymigratePlugin;

import java.util.List;

public class BeanProviderBuilder {

    private final BeanProvider testInstanceBeanProvider;
    private BeanProvider fluentBeanProvider;

    public BeanProviderBuilder(Object testInstance) {
        fluentBeanProvider = new BeanProvider(BeanDefinitions.fromAnnotatedFields(testInstance, BeanHierarchy.INSTANCE));
        testInstanceBeanProvider = fluentBeanProvider;
    }

    public BeanProviderBuilder loadPlugins(List<PluginProvider> providers) {
        providers.forEach(this::loadPlugin);
        return this;
    }

    private void loadPlugin(PluginProvider provider) {
        TrymigratePlugin plugin = provider.get();
        plugin.populate(testInstanceBeanProvider);
        fluentBeanProvider = fluentBeanProvider.append(plugin, BeanHierarchy.PLUGIN);
    }

    public BeanProvider build() {
        return fluentBeanProvider.append(new DefaultBeans(fluentBeanProvider), BeanHierarchy.DEFAULT);
    }

}
