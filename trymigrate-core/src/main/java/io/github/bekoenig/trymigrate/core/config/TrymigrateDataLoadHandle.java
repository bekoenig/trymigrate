package io.github.bekoenig.trymigrate.core.config;

import java.sql.Connection;

public interface TrymigrateDataLoadHandle {

    boolean supports(String resource, String extension);

    void handle(String resource, Connection connection);

}
