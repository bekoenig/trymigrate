package io.github.bekoenig.trymigrate.core.internal.flyway;

import io.github.bekoenig.trymigrate.core.TrymigrateTest;
import io.github.bekoenig.trymigrate.core.internal.jupiter.StoreSupport;
import io.github.bekoenig.trymigrate.core.internal.schemacrawler.lint.LintPatterns;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.MigrationVersion;
import org.flywaydb.core.api.output.MigrateResult;
import org.junit.jupiter.api.extension.ExtensionContext;
import schemacrawler.tools.lint.Lints;

public class FlywayMigrateWrapper {

    private final ExtensionContext extensionContext;

    public FlywayMigrateWrapper(ExtensionContext extensionContext) {
        this.extensionContext = extensionContext;
    }

    public boolean isLatest() {
        return MigrationVersion.LATEST.equals(StoreSupport.getMigrationVersion(extensionContext));
    }

    public void migrate(Flyway flyway, LintPatterns lintPatterns) {
        MigrationVersion lastVersion = StoreSupport.getMigrationVersion(extensionContext);

        MigrateResult migrate = flyway.migrate();
        if (MigrationVersion.fromVersion(migrate.initialSchemaVersion).isNewerThan(flyway.getConfiguration().getTarget())) {
            throw new IllegalStateException("Schema version " + migrate.initialSchemaVersion +
                    " is newer than target " + flyway.getConfiguration().getTarget() +
                    " for test. Dispose database or set " + TrymigrateTest.class.getSimpleName() + "#cleanBefore=true on first migrate test.");
        }

        MigrationVersion currentVersion = MigrationVersion.fromVersion(migrate.targetSchemaVersion);

        if (currentVersion.isNewerThan(lastVersion)) {
            StoreSupport.putMigrationVersion(extensionContext, currentVersion);
            Lints newLints = StoreSupport.getLintsHistory(extensionContext)
                    .diff(lastVersion.getVersion(), currentVersion.getVersion());
            StoreSupport.putLints(extensionContext, newLints);
            StoreSupport.getLintsAssert(extensionContext).assertLints(newLints, lintPatterns);
        }
    }

}
