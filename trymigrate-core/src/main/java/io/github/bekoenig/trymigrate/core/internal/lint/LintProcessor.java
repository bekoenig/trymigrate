package io.github.bekoenig.trymigrate.core.internal.lint;

import io.github.bekoenig.trymigrate.core.internal.lint.config.LinterConfigBuilder;
import io.github.bekoenig.trymigrate.core.internal.lint.config.LintersBuilder;
import io.github.bekoenig.trymigrate.core.lint.config.TrymigrateLintersCustomizer;
import io.github.bekoenig.trymigrate.core.lint.report.TrymigrateLintsReporter;
import org.flywaydb.core.api.MigrationVersion;
import schemacrawler.schema.Catalog;
import schemacrawler.tools.lint.LinterInitializer;
import schemacrawler.tools.lint.Linters;
import schemacrawler.tools.lint.Lints;

import java.sql.Connection;
import java.util.List;

public class LintProcessor {

    private final LinterInitializer linterInitializer;
    private final List<TrymigrateLintersCustomizer> lintersCustomizers;
    private final LintsHistory lintsHistory;
    private final List<TrymigrateLintsReporter> lintsReporters;
    private final LintsAssert lintsAssert;

    public LintProcessor(LinterInitializer linterInitializer,
                         List<TrymigrateLintersCustomizer> lintersCustomizers,
                         LintsHistory lintsHistory,
                         List<TrymigrateLintsReporter> lintsReporters,
                         LintsAssert lintsAssert) {
        this.linterInitializer = linterInitializer;
        this.lintersCustomizers = lintersCustomizers;
        this.lintsHistory = lintsHistory;
        this.lintsReporters = lintsReporters;
        this.lintsAssert = lintsAssert;
    }

    public void lint(Connection connection, String schema, Catalog catalog, MigrationVersion migrationVersion,
                     String tableInclusionPattern, String tableExclusionPattern) {
        LintersBuilder lintersBuilder = LintersBuilder.builder(linterId -> LinterConfigBuilder.builder()
                .linterId(linterId)
                .tableInclusionPattern(tableInclusionPattern)
                .tableExclusionPattern(tableExclusionPattern)
                .runLinter(true));
        lintersCustomizers.forEach(x -> x.accept(lintersBuilder));

        Linters linters = lintersBuilder.build(linterInitializer);
        linters.lint(catalog, connection);
        Lints currentLints = linters.getLints();

        MigrationVersion lastAnalyzedVersion = lintsHistory.getLastAnalyzedVersion();
        lintsHistory.put(migrationVersion, currentLints);

        Lints newLints = lintsHistory.diffLints(lastAnalyzedVersion, migrationVersion);
        lintsReporters.forEach(x -> x.report(catalog, newLints, schema, migrationVersion));
    }

    public boolean isAnalysed(MigrationVersion migrationVersion) {
        return lintsHistory.isAnalysed(migrationVersion);
    }

    public void assertLints(MigrationVersion lastVersion, MigrationVersion currentVersion,
                            LintPatterns suppressedLintPatterns) {
        lintsAssert.assertLints(lintsHistory.diffLints(lastVersion, currentVersion), suppressedLintPatterns);
    }

    public Lints getLints(MigrationVersion currentVersion) {
        return this.lintsHistory.getLints(currentVersion);
    }
}
