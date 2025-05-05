package io.github.bekoenig.trymigrate.database.db2;

import io.github.bekoenig.trymigrate.core.plugin.TrymigrateBean;
import schemacrawler.inclusionrule.ListExclusionRule;
import schemacrawler.schemacrawler.LimitOptions;
import schemacrawler.schemacrawler.LimitOptionsBuilder;

import java.util.List;

public class DefaultDb2Plugin implements Db2Plugin {

    private static final List<String> SYSTEM_SCHEMAS = List.of("NULLID", "SQLJ", "SYSFUN", "SYSIBM",
            "SYSIBMADM", "SYSIBMINTERNAL", "SYSIBMTS", "SYSPROC", "SYSPUBLIC", "SYSSTAT", "SYSTOOLS");

    @TrymigrateBean
    private final LimitOptions limitOptions = LimitOptionsBuilder.builder()
            .includeSchemas(new ListExclusionRule(SYSTEM_SCHEMAS))
            .build();

}
