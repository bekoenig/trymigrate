package io.github.bekoenig.trymigrate.core.lint.report;

import schemacrawler.schema.Catalog;
import schemacrawler.tools.lint.Lints;

public interface LintsReporter {

    void report(Catalog catalog, Lints lints, LintsMigrateInfo migrateInfo);

}
