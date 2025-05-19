package io.github.bekoenig.trymigrate.core.internal.lint.report;

import io.github.bekoenig.trymigrate.core.lint.report.LintsReportResolver;
import org.flywaydb.core.api.MigrationVersion;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;

public class DefaultLintsReportResolver implements LintsReportResolver {

    public static final String PROPERTY_NAME = "trymigrate.lint.reports.path";

    @Override
    public Optional<Path> resolve(String schema, MigrationVersion migrationVersion) {
        Path reportFolder = getBaseFolder()
                .resolve("trymigrate-lint-reports")
                .resolve(Objects.requireNonNullElse(schema, "schema-undefined"));

        try {
            Files.createDirectories(reportFolder);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to create folder", e);
        }

        return Optional.of(reportFolder.resolve(getReportFileName(migrationVersion)));
    }

    private Path getBaseFolder() {
        String property = System.getProperty(PROPERTY_NAME);
        if (property != null) {
            return Path.of(property);
        }
        return getTargetFolder();
    }

    private Path getTargetFolder() {
        try {
            // target folder is parent of system resource folder
            return Path.of(Objects.requireNonNull(ClassLoader.getSystemResource("")).toURI()).getParent();
        } catch (URISyntaxException e) {
            throw new IllegalStateException("Failed to get root uri for system resources of class loader", e);
        }
    }

    private String getReportFileName(MigrationVersion migrationVersion) {
        return migrationVersion.getVersion().replaceAll("\\.", "_") + ".html";
    }

}
