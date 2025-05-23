package io.github.bekoenig.trymigrate.core.internal.lint.report;

import org.flywaydb.core.api.MigrationVersion;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class LintsReportPathResolver {

    public static final String PROPERTY_NAME = "trymigrate.lint.reports.html.basedir";

    public Path resolve(String schema, MigrationVersion migrationVersion) {
        Path reportFolder = getBaseDir()
                .resolve("trymigrate-lint-reports")
                .resolve(Objects.requireNonNullElse(schema, "schema-undefined"));

        try {
            Files.createDirectories(reportFolder);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to create folder", e);
        }

        return reportFolder.resolve(getReportFileName(migrationVersion));
    }

    private Path getBaseDir() {
        String property = System.getProperty(PROPERTY_NAME);
        if (property != null) {
            return Path.of(property);
        }
        return getTargetFolder();
    }

    private Path getTargetFolder() {
        try {
            // target folder is parent of system resource folder
            return Path.of(Objects.requireNonNull(ClassLoader.getSystemResource("./")).toURI()).getParent();
        } catch (URISyntaxException e) {
            throw new IllegalStateException("Failed to get root uri for system resources of class loader", e);
        }
    }

    private String getReportFileName(MigrationVersion migrationVersion) {
        return migrationVersion.getVersion().replaceAll("\\.", "_") + ".html";
    }

}
