package io.github.bekoenig.trymigrate.core.internal.migrate;

import io.github.bekoenig.trymigrate.core.TrymigrateTest;
import io.github.bekoenig.trymigrate.core.internal.catalog.CatalogFactory;
import io.github.bekoenig.trymigrate.core.internal.container.StaticPortBinding;
import io.github.bekoenig.trymigrate.core.internal.lint.LintPatterns;
import io.github.bekoenig.trymigrate.core.internal.lint.LintProcessor;
import io.github.bekoenig.trymigrate.core.internal.migrate.callback.DataLoader;
import io.github.bekoenig.trymigrate.core.internal.migrate.callback.SchemaLinter;
import io.github.bekoenig.trymigrate.core.plugin.customize.TrymigrateDataLoader;
import io.github.bekoenig.trymigrate.core.plugin.customize.TrymigrateFlywayCustomizer;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.MigrationVersion;
import org.flywaydb.core.api.configuration.FluentConfiguration;
import org.flywaydb.core.api.output.MigrateResult;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.lifecycle.Startable;
import schemacrawler.schema.Catalog;
import schemacrawler.tools.lint.Lints;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static io.github.bekoenig.trymigrate.core.plugin.customize.TrymigrateFlywayCustomizer.addCallbacks;
import static org.flywaydb.core.api.MigrationVersion.*;

public class MigrateProcessor {

    private final JdbcDatabaseContainer<?> jdbcDatabaseContainer;
    private final Map<String, String> properties;
    private final List<TrymigrateFlywayCustomizer> flywayCustomizers;
    private final List<TrymigrateDataLoader> dataLoaders;
    private final CatalogFactory catalogFactory;
    private final LintProcessor lintProcessor;

    private DataSource dataSource;
    private Catalog catalog;
    private MigrationVersion currentTarget;

    public MigrateProcessor(JdbcDatabaseContainer<?> jdbcDatabaseContainer, Map<String, String> properties,
                            List<TrymigrateFlywayCustomizer> flywayCustomizers, List<TrymigrateDataLoader> dataLoaders,
                            CatalogFactory catalogFactory, LintProcessor lintProcessor) {
        this.jdbcDatabaseContainer = jdbcDatabaseContainer;
        this.properties = properties;
        this.flywayCustomizers = flywayCustomizers;
        this.dataLoaders = dataLoaders;
        this.catalogFactory = catalogFactory;
        this.lintProcessor = lintProcessor;
    }

    private FluentConfiguration getConfiguration() {
        FluentConfiguration fluentConfiguration = new FluentConfiguration().configuration(properties);
        if (!fluentConfiguration.getTarget().isPredefined()) {
            throw new UnsupportedOperationException("Forcing target for flyway is not allowed. Use @" +
                    TrymigrateTest.class.getSimpleName() + "#whenTarget to force target.");
        }

        flywayCustomizers.forEach(x -> x.accept(fluentConfiguration));
        return fluentConfiguration;
    }

    public void prepare() {
        if (Objects.nonNull(jdbcDatabaseContainer)) {
            new StaticPortBinding().andThen(Startable::start).accept(jdbcDatabaseContainer);
        }

        Flyway flyway = getConfiguration().load();
        flyway.info();
        dataSource = flyway.getConfiguration().getDataSource();

        // assume empty as the initial version
        currentTarget = EMPTY;
    }

    public void migrate(MigrationVersion target, List<String> resources, boolean cleanBefore,
                        LintPatterns suppressedLintPatterns) {
        // nothing to do on latest
        if (LATEST.equals(currentTarget)) {
            return;
        }

        FluentConfiguration fluentConfiguration = getConfiguration();
        fluentConfiguration.target(target);
        addCallbacks(fluentConfiguration, List.of(
                new SchemaLinter(catalogFactory, catalog -> this.catalog = catalog, lintProcessor),
                new DataLoader(dataLoaders, target, resources)));

        Flyway flyway = fluentConfiguration.load();

        if (cleanBefore) {
            flyway.clean();
        }

        MigrationVersion lastTarget = currentTarget;
        MigrateResult migrate = flyway.migrate();
        // target schema version of result is null when current version matches target version
        currentTarget = fromVersion(Objects.requireNonNullElse(migrate.targetSchemaVersion,
                migrate.initialSchemaVersion));
        // throw exception when current version is above target (only happens on database reuse)
        if (currentTarget.isNewerThan(flyway.getConfiguration().getTarget())) {
            throw new IllegalStateException("Schema version " + currentTarget +
                    " is newer than target " + flyway.getConfiguration().getTarget() +
                    " for test. Dispose database or set " + TrymigrateTest.class.getSimpleName() +
                    "#cleanBefore=true on first migrate test.");
        }

        // assert lints only for a newer target
        if (currentTarget.isNewerThan(lastTarget)) {
            lintProcessor.assertLints(lastTarget, currentTarget, suppressedLintPatterns);
        }
    }

    public Lints getLints() {
        return lintProcessor.getLints(currentTarget);
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public Catalog getCatalog() {
        return catalog;
    }

    public void finish() {
        migrate(MigrationVersion.LATEST, List.of(), false, LintPatterns.EMPTY);

        if (Objects.nonNull(jdbcDatabaseContainer)) {
            jdbcDatabaseContainer.stop();
        }
    }
}
