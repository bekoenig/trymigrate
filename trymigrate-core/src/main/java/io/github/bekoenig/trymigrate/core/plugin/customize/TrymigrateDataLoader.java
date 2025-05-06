package io.github.bekoenig.trymigrate.core.plugin.customize;

import java.sql.Connection;

public interface TrymigrateDataLoader {

    boolean supports(String resource, String extension);

    void handle(String resource, Connection connection);

}
