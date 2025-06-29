package io.github.bekoenig.trymigrate.core.internal.migrate.callback;

import io.github.bekoenig.trymigrate.core.internal.catalog.CatalogFactory;
import io.github.bekoenig.trymigrate.core.internal.lint.LintProcessor;
import org.flywaydb.core.api.callback.Callback;
import org.flywaydb.core.api.callback.Context;
import org.flywaydb.core.api.callback.Event;
import schemacrawler.schema.Catalog;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class SchemaLinter implements Callback {

    private final CatalogFactory catalogFactory;
    private final Consumer<Catalog> catalogCache;
    private final LintProcessor lintProcessor;

    public SchemaLinter(CatalogFactory catalogFactory, Consumer<Catalog> catalogCache, LintProcessor lintProcessor) {
        this.catalogFactory = catalogFactory;
        this.catalogCache = catalogCache;
        this.lintProcessor = lintProcessor;
    }

    @Override
    public boolean supports(Event event, Context context) {
        return event == Event.AFTER_EACH_MIGRATE && !lintProcessor.isAnalysed(context.getMigrationInfo().getVersion());
    }

    @Override
    public boolean canHandleInTransaction(Event event, Context context) {
        return false;
    }

    @Override
    public void handle(Event event, Context context) {
        Catalog catalog = catalogFactory.crawl(context.getConnection());
        catalogCache.accept(catalog);

        List<String> schemas = new ArrayList<>();
        schemas.add(context.getConfiguration().getDefaultSchema());
        schemas.addAll(List.of(context.getConfiguration().getSchemas()));

        lintProcessor.lint(
                context.getConnection(),
                context.getConfiguration().getDefaultSchema(),
                catalog,
                context.getMigrationInfo().getVersion(),
                // include all tables from managed schemas
                "(.*\\.)?(" + String.join("|", schemas) + ")\\..*",
                // exclude history table
                context.getConfiguration().getDefaultSchema() + "\\." + context.getConfiguration().getTable()
        );
    }

    @Override
    public String getCallbackName() {
        return getClass().getSimpleName();
    }

}
