package io.github.bekoenig.trymigrate.core.lint;

import io.github.bekoenig.trymigrate.core.internal.schemacrawler.lint.report.MigrateInfo;
import schemacrawler.schema.Catalog;
import schemacrawler.tools.lint.Lints;

public interface LintsReporter {

    void report(Catalog catalog, Lints lints, MigrateInfo migrateInfo);

}
