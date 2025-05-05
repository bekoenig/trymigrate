package io.github.bekoenig.trymigrate.core.internal.bean;

import io.github.bekoenig.trymigrate.core.plugin.TrymigrateBean;
import io.github.bekoenig.trymigrate.core.plugin.TrymigratePlugin;
import org.junit.platform.commons.support.AnnotationSupport;

import java.util.*;
import java.util.stream.Collectors;

public class BeanProviderFactory {

    public BeanProvider create(Object testInstance, List<PluginProvider> pluginProviders) {
        Map<Integer, List<PluginProvider>> layers = pluginProviders.stream()
                .collect(Collectors.groupingBy(PluginProvider::getPriority, Collectors.toList()));

        List<Integer> layerNumbers = layers.keySet().stream()
                .sorted((i, j) -> -Integer.compare(i, j))
                .toList();

        int firstLayer = layerNumbers.isEmpty() ? 0 : layerNumbers.get(0) + 1;
        BeanProvider superior = create(new BeanProvider(List.of()), testInstance, firstLayer);
        for(Integer currentLayerNumber : layerNumbers) {
            BeanProvider current = superior;

            for (PluginProvider provider : layers.get(currentLayerNumber)) {
                TrymigratePlugin plugin = provider.newInstance();
                plugin.populate(superior);
                current = create(current, plugin, currentLayerNumber);
            }

            superior = current;
        }

        return superior;
    }

    private BeanProvider create(BeanProvider beanProvider, Object instance, Integer pluginPriority) {
        List<BeanDefinition> beanDefinitions = new ArrayList<>();
        beanDefinitions.addAll(beanProvider.beanDefinitions());
        beanDefinitions.addAll(AnnotationSupport.findAnnotatedFields(instance.getClass(), TrymigrateBean.class)
                .stream().map(field -> new BeanDefinition(instance, field, pluginPriority)).toList());
        return new BeanProvider(beanDefinitions.stream().sorted().toList());
    }

}
