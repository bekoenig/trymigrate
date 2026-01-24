package io.github.bekoenig.trymigrate.core.internal.plugin;

import io.github.bekoenig.trymigrate.core.plugin.TrymigrateRegisterPlugin;
import org.junit.platform.commons.support.AnnotationSupport;
import org.junit.platform.commons.support.ReflectionSupport;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class PluginRegistryFactory {

    public PluginRegistry create(Object testInstance, List<PluginProvider> pluginProviders) {
        return new PluginRegistry(Stream.concat(
                AnnotationSupport.findAnnotatedFields(testInstance.getClass(), TrymigrateRegisterPlugin.class)
                        .stream()
                        .map(field -> {
                            validate(field);
                            return new PluginProvider(
                                    field.getType(),
                                    () -> getValue(testInstance, field),
                                    Integer.MAX_VALUE);
                        }),
                pluginProviders.stream()).toList());
    }

    private void validate(Field field) {
        if (!PluginTypesValidator.isSupportedType(field.getType())) {
            throw new IllegalArgumentException("Failed to register plugin from field '%s' with unsupported type %s"
                    .formatted(field.getName(), field.getType().getName()));
        }
    }

    private Object getValue(Object testInstance, Field field) {
        Object value = ReflectionSupport.tryToReadFieldValue(field, testInstance)
                .getOrThrow((e) -> new IllegalStateException("Failed to read field " + field.getName()));

        Objects.requireNonNull(value, "%s#%s is null.".formatted(testInstance.getClass().getName(), field.getName()));
        return value;
    }

}
