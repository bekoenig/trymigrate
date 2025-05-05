package io.github.bekoenig.trymigrate.core.internal.bean;

import io.github.bekoenig.trymigrate.core.plugin.TrymigrateBean;
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
    private final Integer pluginPriority;

    public BeanDefinition(Object instance, Field field, Integer pluginPriority) {
        this.instance = instance;
        this.field = field;
        this.pluginPriority = pluginPriority;
    }

    public int getOrder() {
        Order annotation = field.getAnnotation(Order.class);
        if (Objects.isNull(annotation)) {
            return Order.DEFAULT;
        }
        return annotation.value();
    }

    @Override
    public int compareTo(BeanDefinition other) {
        int compareOrder = Integer.compare(this.getOrder(), other.getOrder());
        if (compareOrder != 0) {
            return compareOrder;
        }

        int comparePluginPriority = -Integer.compare(this.pluginPriority, other.pluginPriority);
        if (comparePluginPriority != 0) {
            return comparePluginPriority;
        }

        return Boolean.compare(this.nonNullable(), other.nonNullable());
    }

    public boolean isNullable() {
        TrymigrateBean annotation = field.getAnnotation(TrymigrateBean.class);
        if (Objects.isNull(annotation)) {
            return TrymigrateBean.NULLABLE_DEFAULT;
        }
        return annotation.nullable();
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

        if (!(field.getGenericType() instanceof ParameterizedType genericType)) {
            throw new UnsupportedOperationException("Expects generic for collection type '" + field.getName());
        }

        if (genericType.getActualTypeArguments().length != 1) {
            throw new UnsupportedOperationException("Expects single generic for collection type '" + field.getName());
        }

        return genericType.getActualTypeArguments()[0].equals(clazz);
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
