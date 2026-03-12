package io.github.bekoenig.trymigrate.database.sqlserver;

import io.github.bekoenig.trymigrate.core.Trymigrate;
import io.github.bekoenig.trymigrate.core.TrymigrateWhenTarget;
import io.github.bekoenig.trymigrate.core.lint.TrymigrateSuppressLint;
import io.github.bekoenig.trymigrate.core.lint.TrymigrateVerifyLints;
import io.github.bekoenig.trymigrate.core.plugin.TrymigrateRegisterPlugin;
import io.github.bekoenig.trymigrate.core.plugin.customize.TrymigrateFlywayCustomizer;
import org.junit.jupiter.api.Test;
import org.testcontainers.mssqlserver.MSSQLServerContainer;
import org.testcontainers.utility.DockerImageName;
import schemacrawler.schema.Catalog;
import schemacrawler.tools.lint.LintSeverity;
import schemacrawler.tools.lint.Lints;

import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.assertThat;

@Trymigrate
@TrymigrateVerifyLints(failOn = LintSeverity.medium)
public class ExampleSQLServerSchemaTest {

    @TrymigrateRegisterPlugin
    private final TrymigrateFlywayCustomizer flywayCustomizer = configuration -> configuration
            .defaultSchema("data")
            .locations("classpath:db/migration/example/sqlserver");

    @TrymigrateRegisterPlugin
    private final MSSQLServerContainer containerDatabase = new MSSQLServerContainer(
            DockerImageName.parse("mcr.microsoft.com/mssql/server:2025-latest"))
            .acceptLicense();

    @Test
    @TrymigrateWhenTarget("1.0")
    @TrymigrateSuppressLint(
            linterId = "schemacrawler.tools.linter.LinterTableAllNullableColumns",
            objectName = "master.data.example_entity1")
    @TrymigrateSuppressLint(
            linterId = "schemacrawler.tools.linter.LinterTableWithBadlyNamedColumns",
            objectName = "master.data.example_entity1")
    void initial(DataSource dataSource, Catalog catalog, Lints lints) {
        assertThat(dataSource).isNotNull();
        assertThat(catalog.getSchemas()).isNotEmpty();
        assertThat(lints).isNotEmpty();
    }

}
