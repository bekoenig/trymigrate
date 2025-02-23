package io.github.bekoenig.trymigrate.database.postgresql;

import io.github.bekoenig.trymigrate.core.config.TrymigrateBean;
import io.github.bekoenig.trymigrate.core.lint.config.LintersCustomizer;

import java.util.Map;

public class ExamplePostgreSQLSchemaContainerPlugin extends PostgreSQLContainerPlugin {

    @TrymigrateBean
    private final LintersCustomizer lintersCustomizer = linterConfiguration ->
            linterConfiguration
                    .addConfig(new DummyLinterProvider())
                    .addConfig("schemacrawler.tools.linter.LinterTableSql")
                    .config(Map.of(
                            "message", "message for custom SQL lint",
                            "sql", "SELECT COUNT(1) FROM ${table}"))
                    .addConfig("schemacrawler.tools.linter.LinterTableSql")
                    .config(Map.of(
                            "message", "other message for custom SQL lint",
                            "sql", "SELECT COUNT(1)+4711 FROM ${table}"))
                    .removeAllConfigs("schemacrawler.tools.linter.LinterTableEmpty")
                    .removeAllConfigs("schemacrawler.tools.linter.LinterTableWithBadlyNamedColumns");

}
