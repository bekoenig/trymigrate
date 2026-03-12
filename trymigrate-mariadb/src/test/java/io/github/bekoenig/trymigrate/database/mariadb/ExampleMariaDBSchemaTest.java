package io.github.bekoenig.trymigrate.database.mariadb;

import io.github.bekoenig.trymigrate.core.Trymigrate;
import io.github.bekoenig.trymigrate.core.TrymigrateWhenTarget;
import io.github.bekoenig.trymigrate.core.lint.TrymigrateSuppressLint;
import io.github.bekoenig.trymigrate.core.lint.TrymigrateVerifyLints;
import io.github.bekoenig.trymigrate.core.plugin.TrymigrateRegisterPlugin;
import io.github.bekoenig.trymigrate.core.plugin.customize.TrymigrateFlywayCustomizer;
import org.junit.jupiter.api.Test;
import org.testcontainers.mariadb.MariaDBContainer;
import org.testcontainers.utility.DockerImageName;
import schemacrawler.schema.Catalog;
import schemacrawler.tools.lint.LintSeverity;
import schemacrawler.tools.lint.Lints;

import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.assertThat;

@Trymigrate
@TrymigrateVerifyLints(failOn = LintSeverity.medium)
public class ExampleMariaDBSchemaTest {

    @TrymigrateRegisterPlugin
    private final TrymigrateFlywayCustomizer flywayCustomizer = configuration -> configuration
            .defaultSchema("example")
            .locations("classpath:db/migration/example/mariadb");

    @TrymigrateRegisterPlugin
    private final MariaDBContainer containerDatabase = new MariaDBContainer(
            DockerImageName.parse("mariadb:12.2.2")).withDatabaseName("example");

    @Test
    @TrymigrateWhenTarget("1.0")
    @TrymigrateSuppressLint(
            linterId = "schemacrawler.tools.linter.LinterTableAllNullableColumns",
            objectName = "example.example_entity1")
    @TrymigrateSuppressLint(
            linterId = "schemacrawler.tools.linter.LinterTableWithBadlyNamedColumns",
            objectName = "example.example_entity1")
    @TrymigrateSuppressLint(
            linterId = "schemacrawler.tools.linter.LinterNullIntendedColumns",
            objectName = "example.example_entity1")
    void initial(DataSource dataSource, Catalog catalog, Lints lints) {
        assertThat(dataSource).isNotNull();
        assertThat(catalog.getSchemas()).isNotEmpty();
        assertThat(lints).isNotEmpty();
    }

}
