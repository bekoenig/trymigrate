package io.github.bekoenig.trymigrate.core.lint.report;

import org.flywaydb.core.api.MigrationVersion;
import schemacrawler.schema.Catalog;
import schemacrawler.tools.lint.Lints;

public interface TrymigrateLintsReporter {

    void report(Catalog catalog, Lints lints, String schema, MigrationVersion migrationVersion);

}
