package io.github.bekoenig.trymigrate.core.internal.plugin;

import io.github.bekoenig.trymigrate.core.plugin.customize.*;
import org.flywaydb.core.api.callback.Callback;
import org.flywaydb.core.api.migration.JavaMigration;
import org.testcontainers.containers.JdbcDatabaseContainer;

import java.util.List;
import java.util.function.Supplier;

public class PluginTypesValidator {

    private PluginTypesValidator() {
    }

    /**
     * Supported plugin types. Lazy loaded for optional dependency support.
     */
    private static final List<Supplier<Class<?>>> SUPPORTED_TYPES = List.of(
            () -> TrymigrateCatalogCustomizer.class,
            () -> TrymigrateDataLoader.class,
            () -> TrymigrateFlywayCustomizer.class,
            () -> TrymigrateLintersConfigurer.class,
            () -> TrymigrateLintOptionsCustomizer.class,
            () -> TrymigrateLintsReporter.class,
            () -> JdbcDatabaseContainer.class,
            () -> Callback.class,
            () -> JavaMigration.class
    );

    public static boolean isSupportedType(Class<?> clazz) {
        for (Supplier<Class<?>> supplier : SUPPORTED_TYPES) {
            Class<?> supportedType;
            try {
                supportedType = supplier.get();
            } catch (NoClassDefFoundError e) {
                continue;
            }
            if (supportedType.isAssignableFrom(clazz)) {
                return true;
            }
        }
        return false;
    }

}
