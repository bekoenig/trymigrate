package io.github.bekoenig.trymigrate.core.internal.lint;

import io.github.bekoenig.trymigrate.core.internal.lint.config.RestrictedPattern;
import io.github.bekoenig.trymigrate.core.internal.lint.config.LintersBuilder;
import io.github.bekoenig.trymigrate.core.plugin.customize.TrymigrateLintersConfigurer;
import io.github.bekoenig.trymigrate.core.plugin.customize.TrymigrateLintsReporter;
import org.flywaydb.core.api.MigrationVersion;
import schemacrawler.schema.Catalog;
import schemacrawler.tools.command.lint.options.LintOptions;
import schemacrawler.tools.lint.Linters;
import schemacrawler.tools.lint.Lints;

import java.sql.Connection;
import java.util.List;

public class LintProcessor {

    private final LintPatterns excludedLintPatterns;
    private final List<TrymigrateLintersConfigurer> lintersConfigurers;
    private final LintsHistory lintsHistory;
    private final List<TrymigrateLintsReporter> lintsReporters;
    private final LintOptions lintOptions;
    private final LintsVerifier lintsVerifier;

    public LintProcessor(LintPatterns excludedLintPatterns,
                         List<TrymigrateLintersConfigurer> lintersConfigurers,
                         LintsHistory lintsHistory,
                         List<TrymigrateLintsReporter> lintsReporters,
                         LintOptions lintOptions,
                         LintsVerifier lintsVerifier) {
        this.excludedLintPatterns = excludedLintPatterns;
        this.lintersConfigurers = lintersConfigurers;
        this.lintsHistory = lintsHistory;
        this.lintsReporters = lintsReporters;
        this.lintOptions = lintOptions;
        this.lintsVerifier = lintsVerifier;
    }

    public void lint(Connection connection, String schema, Catalog catalog, MigrationVersion migrationVersion,
                     RestrictedPattern tablePattern) {
        LintersBuilder lintersBuilder = LintersBuilder.builder(tablePattern);
        lintersConfigurers.forEach(x -> x.accept(lintersBuilder));

        Linters linters = lintersBuilder.build();
        linters.lint(catalog, connection);
        Lints currentLints = new Lints(excludedLintPatterns.dropMatching(linters.getLints().stream()).toList());

        Lints newLints = lintsHistory.diffNewLints(migrationVersion, currentLints);
        lintsReporters.forEach(x -> x.report(catalog, newLints, schema, migrationVersion, lintOptions));
    }

    public boolean isAnalysed(MigrationVersion migrationVersion) {
        return lintsHistory.isAnalysed(migrationVersion);
    }

    public void verify(MigrationVersion lastVersion, MigrationVersion currentVersion,
                       LintPatterns suppressedLintPatterns) {
        lintsVerifier.verify(lintsHistory.diffLints(lastVersion, currentVersion), suppressedLintPatterns);
    }

    public Lints getLints(MigrationVersion currentVersion) {
        return lintsHistory.getLints(currentVersion);
    }
}
