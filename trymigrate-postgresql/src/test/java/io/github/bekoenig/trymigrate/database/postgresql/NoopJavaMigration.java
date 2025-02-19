package io.github.bekoenig.trymigrate.database.postgresql;

import org.flywaydb.core.api.MigrationVersion;
import org.flywaydb.core.api.migration.Context;
import org.flywaydb.core.api.migration.JavaMigration;

public class NoopJavaMigration implements JavaMigration {

    private final MigrationVersion version;

    public NoopJavaMigration(String version) {
        this.version = MigrationVersion.fromVersion(version);
    }

    @Override
    public MigrationVersion getVersion() {
        return version;
    }

    @Override
    public String getDescription() {
        return "Noop";
    }

    @Override
    public Integer getChecksum() {
        return null;
    }

    @Override
    public boolean canExecuteInTransaction() {
        return true;
    }

    @Override
    public void migrate(Context context) {
        System.out.println("Noop");
    }
}
