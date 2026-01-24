package io.github.bekoenig.trymigrate.database.postgresql;

import io.github.bekoenig.trymigrate.core.plugin.customize.TrymigrateLintersConfigurer;
import schemacrawler.tools.lint.LintSeverity;

import java.util.Map;

public class ExamplePostgreSQLSchemaTestLintersConfigurer implements TrymigrateLintersConfigurer, ExamplePostgreSQLSchemaTestPlugin {

    @Override
    public void accept(TrymigrateLintersConfiguration lintersConfiguration) {
        lintersConfiguration
                .register(new DummyLinterProvider())
                .disable("schemacrawler.tools.linter.LinterTableWithBadlyNamedColumns")
                .reconfigure("schemacrawler.tools.linter.LinterTableWithNoRemarks").severity(LintSeverity.critical)
                .configure("io.github.bekoenig.trymigrate.database.postgresql.DummyLinter")
                .configure("schemacrawler.tools.linter.LinterTableSql")
                .config(Map.of(
                        "message", "message for custom SQL lint",
                        "sql", "SELECT COUNT(1) FROM ${table}"))
                .configure("schemacrawler.tools.linter.LinterTableSql")
                .config(Map.of(
                        "message", "other message for custom SQL lint",
                        "sql", "SELECT COUNT(1)+4711 FROM ${table}"));
    }

}
