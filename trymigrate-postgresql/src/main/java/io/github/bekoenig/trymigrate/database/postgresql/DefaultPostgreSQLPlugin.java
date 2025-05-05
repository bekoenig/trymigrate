package io.github.bekoenig.trymigrate.database.postgresql;

import io.github.bekoenig.trymigrate.core.plugin.TrymigrateBean;
import schemacrawler.inclusionrule.ListExclusionRule;
import schemacrawler.schemacrawler.LimitOptions;
import schemacrawler.schemacrawler.LimitOptionsBuilder;

import java.util.List;

public class DefaultPostgreSQLPlugin implements PostgreSQLPlugin {

    private static final List<String> SYSTEM_SCHEMAS = List.of("information_schema", "public", "pg_catalog");

    @TrymigrateBean
    private final LimitOptions limitOptions = LimitOptionsBuilder.builder()
            .includeSchemas(new ListExclusionRule(SYSTEM_SCHEMAS))
            .build();

}