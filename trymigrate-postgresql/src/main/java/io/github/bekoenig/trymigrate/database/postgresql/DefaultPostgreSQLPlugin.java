package io.github.bekoenig.trymigrate.database.postgresql;

import io.github.bekoenig.trymigrate.core.plugin.TrymigrateBean;
import io.github.bekoenig.trymigrate.core.plugin.TrymigrateBeanProvider;
import io.github.bekoenig.trymigrate.core.plugin.TrymigratePluginProvider;
import io.github.bekoenig.trymigrate.core.plugin.customize.TrymigrateCatalogCustomizer;
import schemacrawler.inclusionrule.ListExclusionRule;
import schemacrawler.schemacrawler.LimitOptionsBuilder;

import java.util.List;

/**
 * Default plugin implementation for {@link TrymigratePostgreSQLPlugin}.
 */
public class DefaultPostgreSQLPlugin implements TrymigratePostgreSQLPlugin {

    public static class DefaultPostgreSQLPluginProvider implements TrymigratePluginProvider<DefaultPostgreSQLPlugin> {
        @Override
        public DefaultPostgreSQLPlugin provide(TrymigrateBeanProvider beanProvider) {
            return new DefaultPostgreSQLPlugin();
        }
    }

    private static final List<String> SYSTEM_SCHEMAS = List.of("information_schema", "public", "pg_catalog");

    @TrymigrateBean
    private final TrymigrateCatalogCustomizer catalogCustomizer = new TrymigrateCatalogCustomizer() {
        @Override
        public void customize(LimitOptionsBuilder builder) {
            builder.includeSchemas(new ListExclusionRule(SYSTEM_SCHEMAS));
        }
    };

}