package io.github.bekoenig.trymigrate.database.db2;

import io.github.bekoenig.trymigrate.core.plugin.TrymigrateBean;
import io.github.bekoenig.trymigrate.core.plugin.TrymigrateBeanProvider;
import io.github.bekoenig.trymigrate.core.plugin.TrymigratePluginProvider;
import io.github.bekoenig.trymigrate.core.plugin.customize.TrymigrateCatalogCustomizer;
import schemacrawler.inclusionrule.ListExclusionRule;
import schemacrawler.schemacrawler.LimitOptionsBuilder;

import java.util.List;

/**
 * Default plugin implementation for {@link TrymigrateDb2Plugin}.
 */
public class DefaultDb2Plugin implements TrymigrateDb2Plugin {

    public static class DefaultDb2PluginProvider implements TrymigratePluginProvider<DefaultDb2Plugin> {
        @Override
        public DefaultDb2Plugin provide(TrymigrateBeanProvider beanProvider) {
            return new DefaultDb2Plugin();
        }
    }

    private static final List<String> SYSTEM_SCHEMAS = List.of("NULLID", "SQLJ", "SYSFUN", "SYSIBM",
            "SYSIBMADM", "SYSIBMINTERNAL", "SYSIBMTS", "SYSPROC", "SYSPUBLIC", "SYSSTAT", "SYSTOOLS");

    @TrymigrateBean
    private final TrymigrateCatalogCustomizer catalogCustomizer = new TrymigrateCatalogCustomizer() {
        @Override
        public void customize(LimitOptionsBuilder builder) {
            builder.includeSchemas(new ListExclusionRule(SYSTEM_SCHEMAS));
        }
    };

}
