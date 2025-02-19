package io.github.bekoenig.trymigrate.core.config;

public interface TrymigrateDataLoadHandle {

    boolean supports(String classpathResource, String fileExtension);

    void handle(String classpathResource);

}
