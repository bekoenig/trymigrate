package io.github.bekoenig.trymigrate.core.internal.plugin;

import io.github.bekoenig.trymigrate.core.internal.container.JdbcDatabaseContainerAdapter;
import io.github.bekoenig.trymigrate.core.plugin.TrymigrateRegisterPlugin;
import io.github.bekoenig.trymigrate.core.plugin.customize.TrymigrateDatabase;
import org.junit.platform.commons.support.AnnotationSupport;
import org.junit.platform.commons.support.ModifierSupport;
import org.junit.platform.commons.support.ReflectionSupport;
import org.testcontainers.containers.JdbcDatabaseContainer;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class PluginRegistryFactory {

    public static final int TEST_INSTANCE_RANK = Integer.MAX_VALUE;

    public PluginRegistry create(Object testInstance, List<PluginProvider> pluginProviders) {
        return new PluginRegistry(Stream.concat(
                AnnotationSupport.findAnnotatedFields(testInstance.getClass(), TrymigrateRegisterPlugin.class)
                        .stream()
                        .map(field -> toProvider(testInstance, field)),
                pluginProviders.stream()).toList());
    }

    private void validate(Field field) {
        if (!PluginTypesValidator.isSupportedType(field.getType())) {
            throw new IllegalArgumentException("Failed to register plugin from field '%s' with unsupported type %s"
                    .formatted(field.getName(), field.getType().getName()));
        }
    }

    private PluginProvider toProvider(Object testInstance, Field field) {
        Class<?> type = field.getType();

        if (JdbcDatabaseContainer.class.isAssignableFrom(type)) {
            return new PluginProvider(
                    TrymigrateDatabase.class,
                    () -> new JdbcDatabaseContainerAdapter(
                            (JdbcDatabaseContainer<?>) getValue(testInstance, field),
                            ModifierSupport.isStatic(field)),
                    TEST_INSTANCE_RANK
            );
        }

        validate(field);
        return new PluginProvider(type, () -> getValue(testInstance, field), TEST_INSTANCE_RANK);
    }

    private Object getValue(Object testInstance, Field field) {
        Object value = ReflectionSupport.tryToReadFieldValue(field, testInstance)
                .getOrThrow((e) -> new IllegalStateException("Failed to read field " + field.getName()));

        Objects.requireNonNull(value, "%s#%s is null.".formatted(testInstance.getClass().getName(), field.getName()));
        return value;
    }

}
