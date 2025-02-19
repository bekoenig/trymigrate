package io.github.bekoenig.trymigrate.core.internal.bean;

import io.github.bekoenig.trymigrate.core.config.TrymigrateBean;
import org.junit.jupiter.api.Order;
import org.junit.platform.commons.support.ReflectionSupport;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class BeanDefinition implements Comparable<BeanDefinition> {

    private final Object instance;

    private final Field field;

    private final BeanHierarchy hierarchy;

    public BeanDefinition(Object instance, Field field, BeanHierarchy hierarchy) {
        this.instance = instance;
        this.field = field;
        this.hierarchy = hierarchy;
    }

    private Integer getOrder() {
        Order annotation = field.getAnnotation(Order.class);
        if (annotation == null) {
            return Order.DEFAULT;
        }

        return annotation.value();
    }

    @Override
    public int compareTo(BeanDefinition other) {
        Integer thisOrder = this.getOrder();
        Integer otherOrder = other.getOrder();

        // compare priority on same order
        if (Objects.equals(thisOrder, otherOrder)) {
            return this.hierarchy.compareTo(other.hierarchy);
        }

        return Integer.compare(thisOrder, otherOrder);
    }

    private boolean isNullable() {
        return field.getAnnotation(TrymigrateBean.class).nullable();
    }

    public boolean nonNullable() {
        return !isNullable();
    }

    public boolean is(Class<?> clazz) {
        return clazz.isAssignableFrom(field.getType());
    }

    public boolean isCollection(Class<?> clazz) {
        if (!Collection.class.isAssignableFrom(field.getType())) {
            return false;
        }

        ParameterizedType genericType = (ParameterizedType) field.getGenericType();
        return genericType.getActualTypeArguments().length == 1 && genericType.getActualTypeArguments()[0].equals(clazz);
    }

    public <T> T get(Class<T> clazz) {
        Object value = ReflectionSupport.tryToReadFieldValue(field, instance)
                .getOrThrow((e) -> new IllegalStateException("Failed to read field " + field.getName()));

        if (nonNullable()) {
            Objects.requireNonNull(value, instance.getClass().getName() + "#" + field.getName() +
                    " is null. Initialize properly or mark as nullable.");
        }

        return clazz.cast(value);
    }

    @SuppressWarnings("unchecked")
    public <T> Collection<T> getCollection(Class<T> clazz) {
        if (isCollection(clazz)) {
            return (Collection<T>) get(Collection.class);
        }

        T value = get(clazz);
        if (value == null) {
            return Collections.emptyList();
        }
        return List.of(value);
    }

}
