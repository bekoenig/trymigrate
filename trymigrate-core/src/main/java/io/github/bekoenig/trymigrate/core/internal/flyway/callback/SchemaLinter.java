package io.github.bekoenig.trymigrate.core.internal.flyway.callback;

import io.github.bekoenig.trymigrate.core.internal.schemacrawler.catalog.CatalogFactory;
import io.github.bekoenig.trymigrate.core.internal.schemacrawler.lint.LintsHistory;
import io.github.bekoenig.trymigrate.core.internal.schemacrawler.lint.config.LinterConfigBuilder;
import io.github.bekoenig.trymigrate.core.internal.schemacrawler.lint.config.LintersBuilder;
import io.github.bekoenig.trymigrate.core.lint.config.LintersCustomizer;
import io.github.bekoenig.trymigrate.core.lint.report.LintsMigrateInfo;
import io.github.bekoenig.trymigrate.core.lint.report.LintsReporter;
import org.flywaydb.core.api.callback.Callback;
import org.flywaydb.core.api.callback.Context;
import org.flywaydb.core.api.callback.Event;
import schemacrawler.schema.Catalog;
import schemacrawler.tools.lint.Linters;
import schemacrawler.tools.lint.Lints;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class SchemaLinter implements Callback {

    private final List<LintersCustomizer> lintersCustomizers;
    private final CatalogFactory catalogFactory;
    private final Consumer<Catalog> catalogCache;
    private final LintsHistory lintsHistory;
    private final List<LintsReporter> lintsReporters;

    public SchemaLinter(List<LintersCustomizer> lintersCustomizers, CatalogFactory catalogFactory,
                        Consumer<Catalog> catalogCache, LintsHistory lintsHistory, List<LintsReporter> lintsReporters) {
        this.lintersCustomizers = lintersCustomizers;
        this.catalogFactory = catalogFactory;
        this.catalogCache = catalogCache;
        this.lintsHistory = lintsHistory;
        this.lintsReporters = lintsReporters;
    }

    @Override
    public boolean supports(Event event, Context context) {
        return event == Event.AFTER_EACH_MIGRATE
                && !lintsHistory.isAnalysed(context.getMigrationInfo().getVersion().getVersion());
    }

    @Override
    public boolean canHandleInTransaction(Event event, Context context) {
        return false;
    }

    @Override
    public void handle(Event event, Context context) {
        List<String> schemas = new ArrayList<>();
        schemas.add(context.getConfiguration().getDefaultSchema());
        schemas.addAll(List.of(context.getConfiguration().getSchemas()));

        LintersBuilder lintersBuilder = LintersBuilder.builder(linterId -> LinterConfigBuilder.builder()
                .linterId(linterId)
                // include tables from managed schemas
                .tableInclusionPattern("(" + String.join("|", schemas) + ")\\..*")
                // exclude history table
                .tableExclusionPattern(context.getConfiguration().getDefaultSchema() + "\\." +
                        context.getConfiguration().getTable())
                .runLinter(true));
        lintersCustomizers.forEach(lintersCustomizer -> lintersCustomizer.accept(lintersBuilder));

        Catalog catalog = catalogFactory.crawl(context.getConnection());
        catalogCache.accept(catalog);

        Linters linters = lintersBuilder.build();
        linters.lint(catalog, context.getConnection());
        Lints currentLints = linters.getLints();

        String lastAnalyzedVersion = lintsHistory.getLastAnalyzedVersion();
        lintsHistory.putLints(context.getMigrationInfo().getVersion().getVersion(), currentLints);
        Lints newLints = lintsHistory.diff(lastAnalyzedVersion, context.getMigrationInfo().getVersion().getVersion());

        LintsMigrateInfo migrateInfo = new LintsMigrateInfo(
                context.getMigrationInfo().getVersion().getVersion(), context.getConfiguration().getDefaultSchema()
        );
        lintsReporters.forEach(x -> x.report(catalog, newLints, migrateInfo));
    }

    @Override
    public String getCallbackName() {
        return getClass().getSimpleName();
    }

}
