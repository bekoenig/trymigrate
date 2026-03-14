package io.github.bekoenig.trymigrate.core.internal.migrate.callback;

import io.github.bekoenig.trymigrate.core.plugin.customize.TrymigrateDataLoader;
import io.github.bekoenig.trymigrate.core.plugin.customize.TrymigrateDatabase;
import org.flywaydb.core.api.MigrationInfo;
import org.flywaydb.core.api.MigrationVersion;
import org.flywaydb.core.api.callback.Context;
import org.flywaydb.core.api.callback.Event;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class DataLoaderTest {

    private final TrymigrateDatabase database = mock();
    private final TrymigrateDataLoader customLoader = mock();
    private final MigrationVersion version = MigrationVersion.fromVersion("1.0");
    private final List<String> resources = List.of("INSERT INTO test VALUES (1);", "data.csv");
    private final DataLoader dataLoader = new DataLoader(database, List.of(customLoader), version, resources);

    @Test
    @DisplayName("GIVEN a DataLoader WHEN version matches target THEN support BEFORE_EACH_MIGRATE")
    void shouldSupportTargetVersion() {
        // GIVEN
        Context context = mock();
        MigrationInfo migrationInfo = mock();
        when(context.getMigrationInfo()).thenReturn(migrationInfo);
        when(migrationInfo.getVersion()).thenReturn(version);

        // WHEN
        boolean result = dataLoader.supports(Event.BEFORE_EACH_MIGRATE, context);

        // THEN
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("GIVEN a DataLoader WHEN version does not match target THEN NOT support BEFORE_EACH_MIGRATE")
    void shouldNotSupportOtherVersion() {
        // GIVEN
        Context context = mock();
        MigrationInfo migrationInfo = mock();
        when(context.getMigrationInfo()).thenReturn(migrationInfo);
        when(migrationInfo.getVersion()).thenReturn(MigrationVersion.fromVersion("1.1"));

        // WHEN
        boolean result = dataLoader.supports(Event.BEFORE_EACH_MIGRATE, context);

        // THEN
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("GIVEN a DataLoader WHEN handle called THEN load data")
    void shouldHandleLoading() throws Exception {
        // GIVEN
        Context context = mock();
        Connection connection = mock();
        Statement statement = mock();
        when(context.getConnection()).thenReturn(connection);
        when(connection.createStatement()).thenReturn(statement);
        when(customLoader.supports("data.csv", "csv", database)).thenReturn(true);

        // WHEN
        dataLoader.handle(Event.BEFORE_EACH_MIGRATE, context);

        // THEN
        verify(statement).execute("INSERT INTO test VALUES (1);");
        verify(customLoader).load("data.csv", connection, database);
    }

    @Test
    @DisplayName("GIVEN a DataLoader not applied WHEN AFTER_MIGRATE reached THEN throw exception")
    void shouldFailIfMigratedBeyondTargetWithoutApplying() {
        // GIVEN
        Context context = mock();

        // WHEN / THEN
        assertThatThrownBy(() -> dataLoader.supports(Event.AFTER_MIGRATE, context))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Data was not be proceeded because schema is above version 1.0");
    }

}
