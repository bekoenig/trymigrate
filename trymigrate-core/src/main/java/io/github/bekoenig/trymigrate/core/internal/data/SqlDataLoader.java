package io.github.bekoenig.trymigrate.core.internal.data;

import io.github.bekoenig.trymigrate.core.plugin.customize.TrymigrateDataLoader;
import us.fatehi.utility.database.SqlScript;

import java.sql.Connection;

public class SqlDataLoader implements TrymigrateDataLoader {

    @Override
    public boolean supports(String resource, String extension) {
        return extension.equalsIgnoreCase("sql");
    }

    @Override
    public void load(String resource, Connection connection) {
        SqlScript.executeScriptFromResource(resource, connection);
    }

}
