package io.github.bekoenig.trymigrate.core.internal.plugin;

import io.github.bekoenig.trymigrate.core.plugin.TrymigrateBean;
import org.junit.jupiter.api.Order;
import org.junit.platform.commons.support.ReflectionSupport;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class BeanDefinition implements Comparable<BeanDefinition> {

    private final Object instance;
    private final Field field;
    private final Integer rank;

    public BeanDefinition(Object instance, Field field, Integer rank) {
        this.instance = instance;
        this.field = field;
        this.rank = rank;
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
        int compareOrder = Integer.compare(getOrder(), other.getOrder());
        if (compareOrder != 0) {
            return compareOrder;
        }

        int compareRank = -Integer.compare(rank, other.rank);
        if (compareRank != 0) {
            return compareRank;
        }

        return Boolean.compare(nonNullable(), other.nonNullable());
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

    private boolean isCollection() {
        return Collection.class.isAssignableFrom(field.getType());
    }

    public boolean isCompatible(Class<?> clazz) {
        // excepting assignable or collection type
        if (clazz.isAssignableFrom(field.getType())) {
            return true;
        } else if (!isCollection()) {
            return false;
        }

        if (!(field.getGenericType() instanceof ParameterizedType genericType)) {
            throw new UnsupportedOperationException("Expects generic for collection type '" + field.getName());
        }

        if (genericType.getActualTypeArguments().length != 1) {
            throw new UnsupportedOperationException("Expects single generic for collection type '" + field.getName());
        }

        Type actualTypeArgument = genericType.getActualTypeArguments()[0];
        if (actualTypeArgument instanceof Class<?> genericClassType) {
            return clazz.isAssignableFrom(genericClassType);
        } else if (actualTypeArgument instanceof ParameterizedType genericParameterizedType) {
            return clazz.isAssignableFrom(((Class<?>) genericParameterizedType.getRawType()));
        } else {
            return actualTypeArgument.equals(clazz);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> Collection<T> get() {
        Object value = ReflectionSupport.tryToReadFieldValue(field, instance)
                .getOrThrow((e) -> new IllegalStateException("Failed to read field " + field.getName()));

        if (nonNullable()) {
            Objects.requireNonNull(value, instance.getClass().getName() + "#" + field.getName() +
                    " is null. Initialize properly or mark as nullable.");
        }

        if (isCollection()) {
            return (Collection<T>) value;
        }

        if (value == null) {
            return Collections.emptyList();
        }

        return List.of((T) value);
    }

}
