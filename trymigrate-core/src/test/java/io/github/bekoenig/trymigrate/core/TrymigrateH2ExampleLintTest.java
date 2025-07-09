package io.github.bekoenig.trymigrate.core;

import io.github.bekoenig.assertj.schemacrawler.api.SchemaCrawlerAssertions;
import io.github.bekoenig.trymigrate.core.lint.TrymigrateSuppressLint;
import schemacrawler.schema.Catalog;
import schemacrawler.tools.lint.LintSeverity;
import schemacrawler.tools.lint.Lints;

import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.assertThat;

@Trymigrate(flywayProperties = {
        "url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1",
        "defaultSchema=EXAMPLE_SCHEMA",
        "locations=classpath:db/migration/example/h2"
}, failOn = LintSeverity.medium)
public class TrymigrateH2ExampleLintTest {

    @TrymigrateTest(whenTarget = "1.0")
    @TrymigrateSuppressLint(
            objectName = "TESTDB.EXAMPLE_SCHEMA.TAB1", // note: catalog name is part of object names in h2
            linterId = "schemacrawler.tools.linter.LinterTableWithBadlyNamedColumns")
    void initial(DataSource dataSource, Catalog catalog, Lints lints) {
        assertThat(dataSource).isNotNull();
        SchemaCrawlerAssertions.assertThat(catalog).schema("TESTDB.EXAMPLE_SCHEMA").isNotNull();
        assertThat(lints)
                .anyMatch(x -> x.getLinterId().equals("schemacrawler.tools.linter.LinterTableWithNoRemarks"));
    }

}
