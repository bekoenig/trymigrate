package io.github.bekoenig.trymigrate.database.postgresql;

import io.github.bekoenig.trymigrate.core.lint.config.TrymigrateLintersConfigurer;
import io.github.bekoenig.trymigrate.core.plugin.TrymigrateBean;
import io.github.bekoenig.trymigrate.core.plugin.TrymigrateBeanProvider;
import io.github.bekoenig.trymigrate.core.plugin.TrymigratePluginProvider;
import schemacrawler.tools.lint.LintSeverity;

import java.util.Map;

public class ExamplePostgreSQLSchemaTestPlugin implements TrymigratePostgreSQLPlugin {

    public static class ExamplePostgreSQLSchemaTestPluginProvider
            implements TrymigratePluginProvider<ExamplePostgreSQLSchemaTestPlugin> {
        @Override
        public ExamplePostgreSQLSchemaTestPlugin provide(TrymigrateBeanProvider beanProvider) {
            return new ExamplePostgreSQLSchemaTestPlugin();
        }
    }

    @TrymigrateBean
    private final DummyLinterProvider dummyLinterProvider = new DummyLinterProvider();

    @TrymigrateBean
    private final TrymigrateLintersConfigurer lintersConfigurer = linterConfiguration ->
            linterConfiguration
                    .disable("schemacrawler.tools.linter.LinterTableWithBadlyNamedColumns")
                    .reenable("schemacrawler.tools.linter.LinterTableWithNoRemarks").severity(LintSeverity.critical)
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
