package io.github.bekoenig.trymigrate.core.internal.bean;

import io.github.bekoenig.trymigrate.core.config.TrymigrateBean;
import org.junit.platform.commons.support.AnnotationSupport;

import java.util.List;

public class BeanDefinitions {

    private BeanDefinitions() {
    }

    public static List<BeanDefinition> fromAnnotatedFields(Object instance, BeanHierarchy hierarchy) {
        return AnnotationSupport.findAnnotatedFields(instance.getClass(), TrymigrateBean.class).stream()
                .map(field -> new BeanDefinition(instance, field, hierarchy))
                .toList();
    }

}
