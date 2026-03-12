package io.github.bekoenig.trymigrate.database.mysql;

import io.github.bekoenig.trymigrate.core.Trymigrate;
import io.github.bekoenig.trymigrate.core.TrymigrateWhenTarget;
import io.github.bekoenig.trymigrate.core.lint.TrymigrateSuppressLint;
import io.github.bekoenig.trymigrate.core.lint.TrymigrateVerifyLints;
import io.github.bekoenig.trymigrate.core.plugin.TrymigrateRegisterPlugin;
import io.github.bekoenig.trymigrate.core.plugin.customize.TrymigrateFlywayCustomizer;
import org.junit.jupiter.api.Test;
import org.testcontainers.mysql.MySQLContainer;
import org.testcontainers.utility.DockerImageName;
import schemacrawler.schema.Catalog;
import schemacrawler.tools.lint.LintSeverity;
import schemacrawler.tools.lint.Lints;

import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.assertThat;

@Trymigrate
@TrymigrateVerifyLints(failOn = LintSeverity.medium)
public class ExampleMySQLSchemaTest {

    @TrymigrateRegisterPlugin
    private final TrymigrateFlywayCustomizer flywayCustomizer = configuration -> configuration
            .defaultSchema("test")
            .locations("classpath:db/migration/example/mysql");

    @TrymigrateRegisterPlugin
    private final MySQLContainer containerDatabase = new MySQLContainer(
            DockerImageName.parse("mysql:9.4"));

    @Test
    @TrymigrateWhenTarget("1.0")
    @TrymigrateSuppressLint(
            linterId = "schemacrawler.tools.linter.LinterTableAllNullableColumns",
            objectName = "test.example_entity1")
    @TrymigrateSuppressLint(
            linterId = "schemacrawler.tools.linter.LinterTableWithNoRemarks",
            objectName = "test.example_entity1.*")
    @TrymigrateSuppressLint(
            linterId = "schemacrawler.tools.linter.LinterTableWithBadlyNamedColumns",
            objectName = "test.example_entity1")
    void initial(DataSource dataSource, Catalog catalog, Lints lints) {
        assertThat(dataSource).isNotNull();
        assertThat(catalog.getSchemas()).isNotEmpty();
        assertThat(lints).isNotEmpty();
    }

}
