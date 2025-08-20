package io.github.bekoenig.trymigrate.core.internal.migrate.callback;

import org.flywaydb.core.api.callback.Context;
import org.flywaydb.core.api.configuration.Configuration;
import org.flywaydb.core.internal.database.base.Connection;
import org.flywaydb.core.internal.database.base.Database;
import org.flywaydb.core.internal.database.base.Schema;

import java.lang.reflect.Field;
import java.util.Objects;

/**
 * Utilities for {@link Context}.
 */
class ContextSupport {

    /**
     * Field name of connection property in {@link org.flywaydb.core.internal.callback.SimpleContext}.
     */
    private static final String CONNECTION_FIELD_NAME = "connection";

    private ContextSupport() {
    }

    /**
     * Resolve effective default schema from configuration and connection.
     * <p>
     * Implementation in reference to
     * {@link org.flywaydb.core.internal.schemahistory.SchemaHistoryFactory#prepareSchemas(Configuration, Database)}.
     *
     * @param context {@link Context}
     * @return default schema
     */
    static String resolveDefaultSchema(Context context) {
        Configuration configuration = context.getConfiguration();

        String defaultSchema = configuration.getDefaultSchema();
        if (defaultSchema != null) {
            return defaultSchema;
        }

        String[] schemas = configuration.getSchemas();
        if (schemas.length > 0) {
            return schemas[0];
        }

        Schema<?, ?> currentSchema = getInternalConnection(context).getCurrentSchema();
        if (Objects.nonNull(currentSchema) && Objects.nonNull(currentSchema.getName())) {
            return currentSchema.getName();
        }

        throw new IllegalStateException("Unable to resolve default schema");
    }

    /**
     * Get internal connection wrapper of flyway from {@link Context}.
     *
     * @param context {@link Context}
     * @return connection wrapper
     */
    static Connection<?> getInternalConnection(Context context) {
        Field field;
        try {
            field = context.getClass().getDeclaredField(CONNECTION_FIELD_NAME);
        } catch (NoSuchFieldException e) {
            throw new IllegalStateException("Failed to get private field '" + CONNECTION_FIELD_NAME + "'", e);
        }

        field.setAccessible(true);

        Object value;
        try {
            value = field.get(context);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Failed to get value of private field '" + CONNECTION_FIELD_NAME + "'", e);
        }

        if (value instanceof Connection<?> connection) {
            return connection;
        }

        throw new IllegalStateException("Private field '" + CONNECTION_FIELD_NAME + "' is not of type 'Connection<?>'");
    }

}
