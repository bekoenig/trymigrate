package io.github.bekoenig.trymigrate.core.internal.lint;

import io.github.bekoenig.trymigrate.core.internal.lint.config.RestrictedPattern;
import io.github.bekoenig.trymigrate.core.internal.lint.config.LintersBuilder;
import io.github.bekoenig.trymigrate.core.plugin.customize.TrymigrateLintersConfigurer;
import io.github.bekoenig.trymigrate.core.plugin.customize.TrymigrateLintsReporter;
import org.flywaydb.core.api.MigrationVersion;
import schemacrawler.schema.Catalog;
import schemacrawler.tools.command.lint.options.LintOptions;
import schemacrawler.tools.lint.LinterInitializer;
import schemacrawler.tools.lint.Linters;
import schemacrawler.tools.lint.Lints;

import java.sql.Connection;
import java.util.List;

public class LintProcessor {

    private final LintPatterns excludedLintPatterns;
    private final LinterInitializer linterInitializer;
    private final List<TrymigrateLintersConfigurer> lintersConfigurers;
    private final LintsHistory lintsHistory;
    private final List<TrymigrateLintsReporter> lintsReporters;
    private final LintOptions lintOptions;
    private final LintsAssert lintsAssert;

    public LintProcessor(LintPatterns excludedLintPatterns,
                         LinterInitializer linterInitializer,
                         List<TrymigrateLintersConfigurer> lintersConfigurers,
                         LintsHistory lintsHistory,
                         List<TrymigrateLintsReporter> lintsReporters,
                         LintOptions lintOptions,
                         LintsAssert lintsAssert) {
        this.excludedLintPatterns = excludedLintPatterns;
        this.linterInitializer = linterInitializer;
        this.lintersConfigurers = lintersConfigurers;
        this.lintsHistory = lintsHistory;
        this.lintsReporters = lintsReporters;
        this.lintOptions = lintOptions;
        this.lintsAssert = lintsAssert;
    }

    public void lint(Connection connection, String schema, Catalog catalog, MigrationVersion migrationVersion,
                     RestrictedPattern tablePattern) {
        LintersBuilder lintersBuilder = LintersBuilder.builder(tablePattern);
        lintersConfigurers.forEach(x -> x.accept(lintersBuilder));

        Linters linters = lintersBuilder.build(linterInitializer);
        linters.lint(catalog, connection);
        Lints currentLints = new Lints(excludedLintPatterns.dropMatching(linters.getLints().stream()).toList());

        Lints newLints = lintsHistory.diffNewLints(migrationVersion, currentLints);
        lintsReporters.forEach(x -> x.report(catalog, newLints, schema, migrationVersion, lintOptions));
    }

    public boolean isAnalysed(MigrationVersion migrationVersion) {
        return lintsHistory.isAnalysed(migrationVersion);
    }

    public void assertLints(MigrationVersion lastVersion, MigrationVersion currentVersion,
                            LintPatterns suppressedLintPatterns) {
        lintsAssert.assertLints(lintsHistory.diffLints(lastVersion, currentVersion), suppressedLintPatterns);
    }

    public Lints getLints(MigrationVersion currentVersion) {
        return lintsHistory.getLints(currentVersion);
    }
}
