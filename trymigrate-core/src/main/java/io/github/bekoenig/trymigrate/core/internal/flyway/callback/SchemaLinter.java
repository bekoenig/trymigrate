package io.github.bekoenig.trymigrate.core.internal.flyway.callback;

import io.github.bekoenig.trymigrate.core.internal.schemacrawler.catalog.CatalogFactory;
import io.github.bekoenig.trymigrate.core.internal.schemacrawler.lint.LintsHistory;
import io.github.bekoenig.trymigrate.core.internal.schemacrawler.lint.config.LinterConfigBuilder;
import io.github.bekoenig.trymigrate.core.internal.schemacrawler.lint.config.LintersBuilder;
import io.github.bekoenig.trymigrate.core.lint.config.LintersCustomizer;
import io.github.bekoenig.trymigrate.core.lint.report.LintsReporter;
import org.flywaydb.core.api.MigrationVersion;
import org.flywaydb.core.api.callback.Callback;
import org.flywaydb.core.api.callback.Context;
import org.flywaydb.core.api.callback.Event;
import schemacrawler.schema.Catalog;
import schemacrawler.tools.lint.LinterInitializer;
import schemacrawler.tools.lint.Linters;
import schemacrawler.tools.lint.Lints;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class SchemaLinter implements Callback {

    private final LinterInitializer linterInitializer;
    private final LintersCustomizer lintersCustomizer;
    private final CatalogFactory catalogFactory;
    private final Consumer<Catalog> catalogCache;
    private final LintsHistory lintsHistory;
    private final List<LintsReporter> lintsReporters;

    public SchemaLinter(LinterInitializer linterInitializer, LintersCustomizer lintersCustomizer,
                        CatalogFactory catalogFactory, Consumer<Catalog> catalogCache, LintsHistory lintsHistory,
                        List<LintsReporter> lintsReporters) {
        this.linterInitializer = linterInitializer;
        this.lintersCustomizer = lintersCustomizer;
        this.catalogFactory = catalogFactory;
        this.catalogCache = catalogCache;
        this.lintsHistory = lintsHistory;
        this.lintsReporters = lintsReporters;
    }

    @Override
    public boolean supports(Event event, Context context) {
        return event == Event.AFTER_EACH_MIGRATE && !lintsHistory.isAnalysed(context.getMigrationInfo().getVersion());
    }

    @Override
    public boolean canHandleInTransaction(Event event, Context context) {
        return false;
    }

    @Override
    public void handle(Event event, Context context) {
        String defaultSchema = context.getConfiguration().getDefaultSchema();

        List<String> schemas = new ArrayList<>();
        schemas.add(defaultSchema);
        schemas.addAll(List.of(context.getConfiguration().getSchemas()));

        LintersBuilder lintersBuilder = LintersBuilder.builder(linterId -> LinterConfigBuilder.builder()
                .linterId(linterId)
                // include tables from managed schemas
                .tableInclusionPattern("(" + String.join("|", schemas) + ")\\..*")
                // exclude history table
                .tableExclusionPattern(defaultSchema + "\\." +
                        context.getConfiguration().getTable())
                .runLinter(true));
        lintersCustomizer.accept(lintersBuilder);

        Catalog catalog = catalogFactory.crawl(context.getConnection());
        catalogCache.accept(catalog);

        Linters linters = lintersBuilder.build(linterInitializer);
        linters.lint(catalog, context.getConnection());
        Lints currentLints = linters.getLints();

        MigrationVersion lastAnalyzedVersion = lintsHistory.getLastAnalyzedVersion();
        MigrationVersion migrationVersion = context.getMigrationInfo().getVersion();
        lintsHistory.putLints(migrationVersion, currentLints);
        Lints newLints = lintsHistory.diff(lastAnalyzedVersion, migrationVersion);

        lintsReporters.forEach(x -> x.report(catalog, newLints, defaultSchema, migrationVersion));
    }

    @Override
    public String getCallbackName() {
        return getClass().getSimpleName();
    }

}
