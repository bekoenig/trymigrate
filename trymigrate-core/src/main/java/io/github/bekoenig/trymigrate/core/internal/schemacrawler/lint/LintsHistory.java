package io.github.bekoenig.trymigrate.core.internal.schemacrawler.lint;

import org.flywaydb.core.api.MigrationVersion;
import schemacrawler.tools.lint.Lints;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LintsHistory {

    private final LintPatterns ignoredLints;

    private final Map<MigrationVersion, Lints> lints;

    private final List<MigrationVersion> order;

    public LintsHistory(LintPatterns ignoredLints) {
        this.ignoredLints = ignoredLints;
        this.lints = new HashMap<>();
        this.order = new ArrayList<>();
    }

    public boolean isAnalysed(MigrationVersion migrationVersion) {
        return lints.containsKey(migrationVersion);
    }

    public void putLints(MigrationVersion migrationVersion, Lints lints) {
        this.lints.put(migrationVersion, lints);
        this.order.add(migrationVersion);
    }

    public Lints getLints(MigrationVersion migrationVersion) {
        return this.lints.get(migrationVersion);
    }

    public Lints diff(MigrationVersion source, MigrationVersion target) {
        Lints beforeMigrate = getLints(source);
        Lints afterMigrate = getLints(target);

        return new Lints(ignoredLints.dropMatching(afterMigrate.getLints().stream()
                // drop known lints
                .filter(l -> !beforeMigrate.getLints().contains(l)))
                .toList());
    }

    public MigrationVersion getLastAnalyzedVersion() {
        return this.order.get(this.order.size() - 1);
    }

}
