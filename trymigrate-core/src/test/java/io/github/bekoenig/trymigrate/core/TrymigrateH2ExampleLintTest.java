package io.github.bekoenig.trymigrate.core;

import io.github.bekoenig.assertj.schemacrawler.api.SchemaCrawlerAssertions;
import io.github.bekoenig.trymigrate.core.lint.TrymigrateAssertLints;
import io.github.bekoenig.trymigrate.core.lint.TrymigrateSuppressLint;
import io.github.bekoenig.trymigrate.core.plugin.TrymigrateBean;
import io.github.bekoenig.trymigrate.core.plugin.customize.TrymigrateFlywayCustomizer;
import org.junit.jupiter.api.Test;
import schemacrawler.schema.Catalog;
import schemacrawler.schema.Schema;
import schemacrawler.tools.lint.LintSeverity;
import schemacrawler.tools.lint.Lints;

import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.assertThat;

@Trymigrate
@TrymigrateAssertLints(failOn = LintSeverity.medium)
public class TrymigrateH2ExampleLintTest {

    @TrymigrateBean
    private final TrymigrateFlywayCustomizer flywayCustomizer = configuration -> configuration
            .defaultSchema("EXAMPLE_SCHEMA")
            .dataSource("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1", null, null)
            .locations("classpath:db/migration/example/h2");

    @Test
    @TrymigrateWhenTarget("1.0")
    @TrymigrateSuppressLint(
            objectName = "TESTDB.EXAMPLE_SCHEMA.TAB1", // note: catalog name is part of object names in h2
            linterId = "schemacrawler.tools.linter.LinterTableWithBadlyNamedColumns")
    void initial(DataSource dataSource, Catalog catalog, Lints lints) {
        assertThat(dataSource).isNotNull();
        SchemaCrawlerAssertions.assertThat(catalog)
                .schemas()
                .singleElement()
                .extracting(Schema::getName)
                .isEqualTo("EXAMPLE_SCHEMA");
        SchemaCrawlerAssertions.assertThat(catalog).schema("TESTDB.EXAMPLE_SCHEMA").isNotNull();
        assertThat(lints)
                .anyMatch(x -> x.getLinterId().equals("schemacrawler.tools.linter.LinterTableWithNoRemarks"));
    }

    @Test
    void someTest(DataSource dataSource, Catalog catalog, Lints lints) {
        assertThat(dataSource).isNotNull();
        assertThat(catalog).isNotNull();
        assertThat(lints.isEmpty()).isFalse();
    }

}
