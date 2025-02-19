package io.github.bekoenig.trymigrate.core.lint;

import io.github.bekoenig.trymigrate.core.internal.schemacrawler.lint.report.MigrateInfo;

import java.nio.file.Path;
import java.util.Optional;

public interface LintsReportResolver {

    Optional<Path> resolve(MigrateInfo migrateInfo);

}
