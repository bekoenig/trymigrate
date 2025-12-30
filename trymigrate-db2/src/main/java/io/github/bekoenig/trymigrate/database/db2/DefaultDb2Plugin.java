package io.github.bekoenig.trymigrate.database.db2;

import io.github.bekoenig.trymigrate.core.plugin.TrymigratePluginProvider;

/**
 * Default plugin implementation for {@link TrymigrateDb2Plugin}.
 */
public class DefaultDb2Plugin implements TrymigrateDb2Plugin {

    public static class DefaultDb2PluginProvider implements TrymigratePluginProvider<DefaultDb2Plugin> {
        @Override
        public DefaultDb2Plugin provide() {
            return new DefaultDb2Plugin();
        }
    }

}
