package io.github.bekoenig.trymigrate.core.internal.data;

import io.github.bekoenig.trymigrate.core.plugin.TrymigratePlugin;
import io.github.bekoenig.trymigrate.core.plugin.customize.TrymigrateDataLoader;
import us.fatehi.utility.database.SqlScript;

import java.sql.Connection;

/**
 * Plugin to load data from SQL resources in classpath.
 */
public class SqlDataLoader implements TrymigrateDataLoader, TrymigratePlugin {

    @Override
    public boolean supports(String resource, String extension) {
        return extension.equalsIgnoreCase("sql");
    }

    @Override
    public void load(String resource, Connection connection) {
        SqlScript.executeScriptFromResource(resource, connection);
    }

}
