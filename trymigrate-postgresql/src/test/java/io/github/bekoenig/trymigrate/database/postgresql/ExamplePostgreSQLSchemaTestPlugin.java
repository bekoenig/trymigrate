package io.github.bekoenig.trymigrate.database.postgresql;

import io.github.bekoenig.trymigrate.core.lint.config.TrymigrateLintersConfigurer;
import io.github.bekoenig.trymigrate.core.plugin.TrymigrateBean;
import io.github.bekoenig.trymigrate.core.plugin.TrymigratePluginProvider;
import schemacrawler.tools.lint.LintSeverity;

import java.util.Map;

public class ExamplePostgreSQLSchemaTestPlugin implements TrymigratePostgreSQLPlugin {

    public static class ExamplePostgreSQLSchemaTestPluginProvider
            implements TrymigratePluginProvider<ExamplePostgreSQLSchemaTestPlugin> {
        @Override
        public ExamplePostgreSQLSchemaTestPlugin provide() {
            return new ExamplePostgreSQLSchemaTestPlugin();
        }
    }

    @TrymigrateBean
    private final DummyLinterProvider dummyLinterProvider = new DummyLinterProvider();

    @TrymigrateBean
    private final TrymigrateLintersConfigurer lintersConfigurer = linterConfiguration ->
            linterConfiguration
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
