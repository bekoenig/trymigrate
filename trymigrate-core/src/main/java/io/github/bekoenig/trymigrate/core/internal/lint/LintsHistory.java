package io.github.bekoenig.trymigrate.core.internal.lint;

import org.flywaydb.core.api.MigrationVersion;
import schemacrawler.tools.lint.Lints;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LintsHistory {

    private final Map<MigrationVersion, Lints> lints;
    private final List<MigrationVersion> order;

    public LintsHistory() {
        this.lints = new HashMap<>();
        this.order = new ArrayList<>();

        put(MigrationVersion.EMPTY, new Lints(List.of()));
    }

    protected void put(MigrationVersion migrationVersion, Lints lints) {
        this.lints.put(migrationVersion, lints);
        this.order.add(migrationVersion);
    }

    public boolean isAnalysed(MigrationVersion migrationVersion) {
        return lints.containsKey(migrationVersion);
    }

    public Lints getLints(MigrationVersion migrationVersion) {
        return lints.get(migrationVersion);
    }

    public MigrationVersion getLastAnalyzedVersion() {
        return order.get(order.size() - 1);
    }

    public Lints diffLints(MigrationVersion source, MigrationVersion target) {
        Lints beforeMigrate = getLints(source);
        Lints afterMigrate = getLints(target);

        return new Lints(afterMigrate.getLints().stream()
                // drop known lints
                .filter(l -> !beforeMigrate.getLints().contains(l))
                .toList());
    }

    public Lints diffNewLints(MigrationVersion migrationVersion, Lints lints) {
        MigrationVersion lastAnalyzedVersion = getLastAnalyzedVersion();
        put(migrationVersion, lints);
        return diffLints(lastAnalyzedVersion, migrationVersion);
    }

}
