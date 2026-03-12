package io.github.bekoenig.trymigrate.database.oracle;

import io.github.bekoenig.trymigrate.core.Trymigrate;
import io.github.bekoenig.trymigrate.core.TrymigrateWhenTarget;
import io.github.bekoenig.trymigrate.core.lint.TrymigrateSuppressLint;
import io.github.bekoenig.trymigrate.core.lint.TrymigrateVerifyLints;
import io.github.bekoenig.trymigrate.core.plugin.TrymigrateRegisterPlugin;
import io.github.bekoenig.trymigrate.core.plugin.customize.TrymigrateFlywayCustomizer;
import org.junit.jupiter.api.Test;
import org.testcontainers.oracle.OracleContainer;
import org.testcontainers.utility.DockerImageName;
import schemacrawler.schema.Catalog;
import schemacrawler.tools.lint.LintSeverity;
import schemacrawler.tools.lint.Lints;

import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.assertThat;

@Trymigrate
@TrymigrateVerifyLints(failOn = LintSeverity.medium)
public class ExampleOracleSchemaTest {

    @TrymigrateRegisterPlugin
    private final TrymigrateFlywayCustomizer flywayCustomizer = configuration -> configuration
            .defaultSchema("TEST")
            .locations("classpath:db/migration/example/oracle")
            .createSchemas(false);

    @TrymigrateRegisterPlugin
    private final OracleContainer containerDatabase = new OracleContainer(
            DockerImageName.parse("gvenzl/oracle-free:23.26.1"));

    @Test
    @TrymigrateWhenTarget("1.0")
    @TrymigrateSuppressLint(
            linterId = "schemacrawler.tools.linter.LinterTableAllNullableColumns",
            objectName = "TEST.EXAMPLE_ENTITY1")
    @TrymigrateSuppressLint(
            linterId = "schemacrawler.tools.linter.LinterTableWithBadlyNamedColumns",
            objectName = "TEST.EXAMPLE_ENTITY1")
    void initial(DataSource dataSource, Catalog catalog, Lints lints) {
        assertThat(dataSource).isNotNull();
        assertThat(catalog.getSchemas()).isNotEmpty();
        assertThat(lints).isNotEmpty();
    }

}
