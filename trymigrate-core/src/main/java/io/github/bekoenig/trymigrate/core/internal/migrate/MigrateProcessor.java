package io.github.bekoenig.trymigrate.core.internal.migrate;

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
import org.flywaydb.core.api.callback.Callback;
import org.flywaydb.core.api.configuration.FluentConfiguration;
import org.flywaydb.core.api.migration.JavaMigration;
import org.flywaydb.core.api.output.MigrateResult;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.lifecycle.Startable;
import schemacrawler.schema.Catalog;
import schemacrawler.tools.lint.Lints;

import javax.sql.DataSource;
import java.util.List;
import java.util.Objects;

import static io.github.bekoenig.trymigrate.core.plugin.customize.TrymigrateFlywayCustomizer.addCallbacks;
import static io.github.bekoenig.trymigrate.core.plugin.customize.TrymigrateFlywayCustomizer.addJavaMigrations;
import static org.flywaydb.core.api.MigrationVersion.*;

public class MigrateProcessor {

    private final JdbcDatabaseContainer<?> jdbcDatabaseContainer;
    private final List<TrymigrateFlywayCustomizer> flywayCustomizers;
    private final List<Callback> callbacks;
    private final List<JavaMigration> javaMigrations;
    private final List<TrymigrateDataLoader> dataLoaders;
    private final CatalogFactory catalogFactory;
    private final LintProcessor lintProcessor;

    private DataSource dataSource;
    private Catalog catalog;
    private MigrationVersion currentTarget;

    public MigrateProcessor(JdbcDatabaseContainer<?> jdbcDatabaseContainer,
                            List<TrymigrateFlywayCustomizer> flywayCustomizers, List<Callback> callbacks,
                            List<JavaMigration> javaMigrations, List<TrymigrateDataLoader> dataLoaders,
                            CatalogFactory catalogFactory, LintProcessor lintProcessor) {
        this.jdbcDatabaseContainer = jdbcDatabaseContainer;
        this.flywayCustomizers = flywayCustomizers;
        this.callbacks = callbacks;
        this.javaMigrations = javaMigrations;
        this.dataLoaders = dataLoaders;
        this.catalogFactory = catalogFactory;
        this.lintProcessor = lintProcessor;
    }

    private FluentConfiguration getConfiguration() {
        FluentConfiguration fluentConfiguration = new FluentConfiguration();
        if (Objects.nonNull(jdbcDatabaseContainer)) {
            fluentConfiguration.dataSource(
                    jdbcDatabaseContainer.getJdbcUrl(),
                    jdbcDatabaseContainer.getUsername(),
                    jdbcDatabaseContainer.getPassword());
        }
        flywayCustomizers.forEach(x -> x.accept(fluentConfiguration));
        addCallbacks(fluentConfiguration, callbacks);
        addJavaMigrations(fluentConfiguration, javaMigrations);
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

            // assume empty on clean
            currentTarget = EMPTY;
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
                    " for test.");
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
        if (Objects.nonNull(jdbcDatabaseContainer)) {
            jdbcDatabaseContainer.stop();
        }
    }
}
