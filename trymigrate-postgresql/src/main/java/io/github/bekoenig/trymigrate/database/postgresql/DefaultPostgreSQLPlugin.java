package io.github.bekoenig.trymigrate.database.postgresql;

import io.github.bekoenig.trymigrate.core.plugin.TrymigrateBeanProvider;
import io.github.bekoenig.trymigrate.core.plugin.TrymigratePluginProvider;

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

}