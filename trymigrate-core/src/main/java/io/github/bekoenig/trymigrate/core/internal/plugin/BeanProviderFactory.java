package io.github.bekoenig.trymigrate.core.internal.plugin;

import io.github.bekoenig.trymigrate.core.plugin.TrymigrateBean;
import io.github.bekoenig.trymigrate.core.plugin.TrymigratePlugin;
import org.junit.platform.commons.support.AnnotationSupport;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BeanProviderFactory {

    public BeanProvider create(Object testInstance, List<GenericPluginProvider> pluginProviders) {
        Map<Integer, List<GenericPluginProvider>> layers = pluginProviders.stream()
                .collect(Collectors.groupingBy(GenericPluginProvider::getRank, Collectors.toList()));

        List<Integer> layerRanks = layers.keySet().stream()
                .sorted((i, j) -> -Integer.compare(i, j))
                .toList();

        int testInstanceRank = layerRanks.isEmpty() ? 0 : layerRanks.get(0) + 1;
        BeanProvider lastLayerBeans = create(new BeanProvider(List.of()), testInstance, testInstanceRank);
        for (Integer currentLayerRank : layerRanks) {
            BeanProvider currentLayerBeans = lastLayerBeans;

            for (GenericPluginProvider provider : layers.get(currentLayerRank)) {
                TrymigratePlugin plugin = provider.provide();
                currentLayerBeans = create(currentLayerBeans, plugin, currentLayerRank);
            }

            lastLayerBeans = currentLayerBeans;
        }

        return lastLayerBeans;
    }

    private BeanProvider create(BeanProvider beanProvider, Object instance, Integer rank) {
        List<BeanDefinition> beanDefinitions = new ArrayList<>();
        beanDefinitions.addAll(beanProvider.beanDefinitions());
        beanDefinitions.addAll(AnnotationSupport.findAnnotatedFields(instance.getClass(), TrymigrateBean.class)
                .stream().map(field -> new BeanDefinition(instance, field, rank)).toList());
        return new BeanProvider(beanDefinitions.stream().sorted().toList());
    }

}
