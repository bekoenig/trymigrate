package io.github.bekoenig.trymigrate.database.postgresql;

import io.github.bekoenig.trymigrate.core.plugin.TrymigrateBean;
import io.github.bekoenig.trymigrate.core.lint.config.TrymigrateLintersConfigurer;

import java.util.Map;

public class ExamplePostgreSQLSchemaTestPlugin implements TrymigratePostgreSQLPlugin {

    @TrymigrateBean
    private final DummyLinterProvider dummyLinterProvider = new DummyLinterProvider();

    @TrymigrateBean
    private final TrymigrateLintersConfigurer lintersConfigurer = linterConfiguration ->
            linterConfiguration
                    .enable("io.github.bekoenig.trymigrate.database.postgresql.DummyLinter")
                    .enable("schemacrawler.tools.linter.LinterTableSql")
                    .config(Map.of(
                            "message", "message for custom SQL lint",
                            "sql", "SELECT COUNT(1) FROM ${table}"))
                    .enable("schemacrawler.tools.linter.LinterTableSql")
                    .config(Map.of(
                            "message", "other message for custom SQL lint",
                            "sql", "SELECT COUNT(1)+4711 FROM ${table}"));
}
