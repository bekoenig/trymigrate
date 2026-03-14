package io.github.bekoenig.trymigrate.core.internal.migrate.callback;

import io.github.bekoenig.trymigrate.core.internal.catalog.CatalogFactory;
import io.github.bekoenig.trymigrate.core.internal.lint.LintProcessor;
import io.github.bekoenig.trymigrate.core.internal.lint.config.RestrictedPattern;
import org.flywaydb.core.api.MigrationInfo;
import org.flywaydb.core.api.MigrationVersion;
import org.flywaydb.core.api.callback.Context;
import org.flywaydb.core.api.callback.Event;
import org.flywaydb.core.api.configuration.Configuration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import schemacrawler.schema.Catalog;

import java.sql.Connection;
import java.util.Set;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class SchemaLinterTest {

    private final CatalogFactory catalogFactory = mock();
    private final Consumer<Catalog> catalogCache = mock();
    private final LintProcessor lintProcessor = mock();
    private final SchemaLinter schemaLinter = new SchemaLinter(catalogFactory, catalogCache, lintProcessor);

    @Test
    @DisplayName("GIVEN an AFTER_EACH_MIGRATE event WHEN version not analysed THEN return true")
    void shouldSupportAfterEachMigrate() {
        // GIVEN
        Context context = mock();
        MigrationInfo migrationInfo = mock();
        MigrationVersion version = MigrationVersion.fromVersion("1.0");
        when(context.getMigrationInfo()).thenReturn(migrationInfo);
        when(migrationInfo.getVersion()).thenReturn(version);
        when(lintProcessor.isAnalysed(version)).thenReturn(false);

        // WHEN
        boolean result = schemaLinter.supports(Event.AFTER_EACH_MIGRATE, context);

        // THEN
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("GIVEN an AFTER_EACH_MIGRATE event WHEN handle called THEN crawl and lint")
    void shouldHandleAfterEachMigrate() {
        // GIVEN
        Context context = mock();
        Configuration configuration = mock();
        MigrationInfo migrationInfo = mock();
        MigrationVersion version = MigrationVersion.fromVersion("1.0");
        Connection connection = mock();
        Catalog catalog = mock();

        when(context.getConfiguration()).thenReturn(configuration);
        when(context.getMigrationInfo()).thenReturn(migrationInfo);
        when(migrationInfo.getVersion()).thenReturn(version);
        when(context.getConnection()).thenReturn(connection);
        when(configuration.getSchemas()).thenReturn(new String[]{"SCHEMA1"});
        when(configuration.getTable()).thenReturn("flyway_history");
        when(catalogFactory.crawl(eq(connection), any(Set.class))).thenReturn(catalog);

        // WHEN
        schemaLinter.handle(Event.AFTER_EACH_MIGRATE, context);

        // THEN
        verify(catalogCache).accept(catalog);
        verify(lintProcessor).lint(eq(connection), any(), eq(catalog), eq(version), any(RestrictedPattern.class));
    }

}
