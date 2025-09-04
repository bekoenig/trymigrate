package io.github.bekoenig.trymigrate.core.internal.plugin;

import io.github.bekoenig.trymigrate.core.plugin.TrymigrateBean;
import io.github.bekoenig.trymigrate.core.plugin.TrymigratePlugin;
import org.junit.platform.commons.support.AnnotationSupport;

import java.util.*;
import java.util.stream.Collectors;

public class BeanProviderFactory {

    public BeanProvider create(Object testInstance, List<GenericPluginProvider> pluginProviders) {
        Map<Integer, List<GenericPluginProvider>> layers = pluginProviders.stream()
                .collect(Collectors.groupingBy(GenericPluginProvider::getHierarchy, Collectors.toList()));

        List<Integer> layerIndex = layers.keySet().stream()
                .sorted((i, j) -> -Integer.compare(i, j))
                .toList();

        int testInstanceHierarchy = layerIndex.isEmpty() ? 0 : layerIndex.get(0) + 1;
        BeanProvider lastLayerBeans = create(new BeanProvider(List.of()), testInstance, testInstanceHierarchy);
        for(Integer currentLayerNumber : layerIndex) {
            BeanProvider currentLayerBeans = lastLayerBeans;

            for (GenericPluginProvider provider : layers.get(currentLayerNumber)) {
                TrymigratePlugin plugin = provider.provide(lastLayerBeans);
                currentLayerBeans = create(currentLayerBeans, plugin, currentLayerNumber);
            }

            lastLayerBeans = currentLayerBeans;
        }

        return lastLayerBeans;
    }

    private BeanProvider create(BeanProvider beanProvider, Object instance, Integer hierarchy) {
        List<BeanDefinition> beanDefinitions = new ArrayList<>();
        beanDefinitions.addAll(beanProvider.beanDefinitions());
        beanDefinitions.addAll(AnnotationSupport.findAnnotatedFields(instance.getClass(), TrymigrateBean.class)
                .stream().map(field -> new BeanDefinition(instance, field, hierarchy)).toList());
        return new BeanProvider(beanDefinitions.stream().sorted().toList());
    }

}
