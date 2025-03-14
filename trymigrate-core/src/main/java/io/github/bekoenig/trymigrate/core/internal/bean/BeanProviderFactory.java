package io.github.bekoenig.trymigrate.core.internal.bean;

import io.github.bekoenig.trymigrate.core.config.TrymigrateBean;
import io.github.bekoenig.trymigrate.core.config.TrymigratePlugin;
import org.junit.platform.commons.support.AnnotationSupport;

import java.util.ArrayList;
import java.util.List;

public class BeanProviderFactory {

    private BeanProvider append(BeanProvider beanProvider, Object instance, BeanHierarchy hierarchy) {
        List<BeanDefinition> beanDefinitions = new ArrayList<>();
        beanDefinitions.addAll(beanProvider.getBeanDefinitions());
        beanDefinitions.addAll(AnnotationSupport.findAnnotatedFields(instance.getClass(), TrymigrateBean.class).stream()
                .map(field -> new BeanDefinition(instance, field, hierarchy)).toList());
        return new BeanProvider(beanDefinitions.stream().sorted().toList());
    }

    public BeanProvider create(Object testInstance, List<PluginProvider> providers) {
        BeanProvider testInstanceBeanProvider = append(new BeanProvider(List.of()), testInstance, BeanHierarchy.INSTANCE);
        BeanProvider fluentBeanProvider = testInstanceBeanProvider;

        for (PluginProvider provider : providers) {
            TrymigratePlugin plugin = provider.get();
            plugin.populate(testInstanceBeanProvider);
            fluentBeanProvider = append(fluentBeanProvider, plugin, BeanHierarchy.PLUGIN);
        }

        return append(fluentBeanProvider, new DefaultBeans(fluentBeanProvider), BeanHierarchy.DEFAULT);
    }

}
