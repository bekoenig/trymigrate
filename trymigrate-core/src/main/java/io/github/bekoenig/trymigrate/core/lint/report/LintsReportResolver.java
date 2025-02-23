package io.github.bekoenig.trymigrate.core.lint.report;

import java.nio.file.Path;
import java.util.Optional;

public interface LintsReportResolver {

    Optional<Path> resolve(LintsMigrateInfo migrateInfo);

}
