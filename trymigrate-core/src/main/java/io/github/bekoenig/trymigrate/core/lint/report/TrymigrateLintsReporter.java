package io.github.bekoenig.trymigrate.core.lint.report;

import org.flywaydb.core.api.MigrationVersion;
import schemacrawler.schema.Catalog;
import schemacrawler.tools.lint.Lints;

/**
 * Reporter for new lints.
 */
public interface TrymigrateLintsReporter {

    /**
     * Reports new lints.
     *
     * @param catalog current {@link Catalog}
     * @param lints new {@link Lints}
     * @param schema current Schema-Name
     * @param migrationVersion current {@link MigrationVersion}
     */
    void report(Catalog catalog, Lints lints, String schema, MigrationVersion migrationVersion);

}
