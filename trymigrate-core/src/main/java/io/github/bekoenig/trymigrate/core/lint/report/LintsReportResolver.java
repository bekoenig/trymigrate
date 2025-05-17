package io.github.bekoenig.trymigrate.core.lint.report;

import org.flywaydb.core.api.MigrationVersion;

import java.nio.file.Path;
import java.util.Optional;

public interface LintsReportResolver {

    Optional<Path> resolve(String schema, MigrationVersion migrationVersion);

}
