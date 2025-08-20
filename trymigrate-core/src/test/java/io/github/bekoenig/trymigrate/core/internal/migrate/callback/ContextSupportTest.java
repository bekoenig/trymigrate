package io.github.bekoenig.trymigrate.core.internal.migrate.callback;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.callback.Callback;
import org.flywaydb.core.api.callback.Context;
import org.flywaydb.core.api.callback.Event;
import org.flywaydb.core.api.configuration.FluentConfiguration;
import org.flywaydb.core.internal.database.base.Connection;
import org.flywaydb.core.internal.database.h2.H2Connection;
import org.junit.jupiter.api.Test;

import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;

class ContextSupportTest {

    @Test
    void resolveDefaultSchema_fromDefaultSchemaProperty() {
        assertContext(
                // GIVEN
                configuration -> configuration
                        .defaultSchema("DEFAULT_SCHEMA_PROPERTY")
                        .dataSource("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1", null, null),
                context -> {
                    // WHEN
                    String defaultSchema = ContextSupport.resolveDefaultSchema(context);

                    // THEN
                    assertThat(defaultSchema).isEqualTo("DEFAULT_SCHEMA_PROPERTY");
                }
        );
    }

    @Test
    void resolveDefaultSchema_fromSchemasProperty() {
        assertContext(
                // GIVEN
                configuration -> configuration
                        .schemas("FIRST_SCHEMA", "SECOND_SCHEMA")
                        .dataSource("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1", null, null),
                context -> {
                    // WHEN
                    String defaultSchema = ContextSupport.resolveDefaultSchema(context);

                    // THEN
                    assertThat(defaultSchema).isEqualTo("FIRST_SCHEMA");
                }
        );
    }

    @Test
    void resolveDefaultSchema_fromConnection() {
        assertContext(
                // GIVEN
                configuration -> configuration
                        .dataSource("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;INIT=CREATE SCHEMA IF NOT EXISTS FROM_CONNECTION\\;SET SCHEMA FROM_CONNECTION", null, null),
                context -> {
                    // WHEN
                    String defaultSchema = ContextSupport.resolveDefaultSchema(context);

                    // THEN
                    assertThat(defaultSchema).isEqualTo("FROM_CONNECTION");
                }
        );
    }

    @Test
    void getInternalConnection() {
        assertContext(
                // GIVEN
                configuration -> configuration
                        .dataSource("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1", null, null),
                context -> {
                    // WHEN
                    Connection<?> internalConnection = ContextSupport.getInternalConnection(context);

                    // THEN
                    assertThat(internalConnection).isInstanceOf(H2Connection.class);
                }
        );
    }

    private static void assertContext(Consumer<FluentConfiguration> configurationConsumer, Consumer<Context> assertion) {
        FluentConfiguration configuration = Flyway.configure();
        configurationConsumer.accept(configuration);

        configuration
                .locations("classpath:db/migration/example/h2")
                .callbacks(new Callback() {
                    @Override
                    public boolean supports(Event event, Context context) {
                        return event == Event.AFTER_MIGRATE;
                    }

                    @Override
                    public boolean canHandleInTransaction(Event event, Context context) {
                        return true;
                    }

                    @Override
                    public void handle(Event event, Context context) {
                        assertion.accept(context);
                    }

                    @Override
                    public String getCallbackName() {
                        return "ContextSupportTest";
                    }
                })
                .load()
                .migrate();
    }
}