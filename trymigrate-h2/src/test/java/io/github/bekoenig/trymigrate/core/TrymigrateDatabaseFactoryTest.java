package io.github.bekoenig.trymigrate.core;

import io.github.bekoenig.assertj.schemacrawler.api.SchemaCrawlerAssertions;
import io.github.bekoenig.trymigrate.core.plugin.TrymigrateRegisterPlugin;
import io.github.bekoenig.trymigrate.core.plugin.customize.TrymigrateDatabase;
import io.github.bekoenig.trymigrate.core.plugin.customize.TrymigrateFlywayCustomizer;
import org.junit.jupiter.api.Test;
import schemacrawler.schema.Catalog;

import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.assertThat;

@Trymigrate
class TrymigrateDatabaseFactoryTest {

    @TrymigrateRegisterPlugin
    private final TrymigrateDatabase database = TrymigrateDatabase.of(
            "jdbc:h2:mem:testdb_factory;DB_CLOSE_DELAY=-1",
            null,
            null
    );

    @TrymigrateRegisterPlugin
    private final TrymigrateFlywayCustomizer flywayCustomizer = configuration -> configuration
            .defaultSchema("EXAMPLE_SCHEMA")
            .locations("classpath:db/migration/example/h2");

    @Test
    @TrymigrateWhenTarget("1.0")
    void shouldUseDatabaseFactory(DataSource dataSource, Catalog catalog) {
        assertThat(dataSource).isNotNull();
        SchemaCrawlerAssertions.assertThat(catalog).schema("TESTDB_FACTORY.EXAMPLE_SCHEMA").isNotNull();
    }
}
