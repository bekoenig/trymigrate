package io.github.bekoenig.trymigrate.core.internal.migrate.callback;

import io.github.bekoenig.trymigrate.core.internal.catalog.CatalogFactory;
import io.github.bekoenig.trymigrate.core.internal.lint.LintProcessor;
import io.github.bekoenig.trymigrate.core.internal.lint.config.DefaultablePattern;
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

        String defaultSchema = context.getConfiguration().getDefaultSchema();

        List<String> schemas = new ArrayList<>();
        schemas.add(defaultSchema);
        schemas.addAll(List.of(context.getConfiguration().getSchemas()));

        DefaultablePattern defaultTablePattern = new DefaultablePattern(
                // include all tables from managed schemas
                "(.*\\.)?(" + String.join("|", schemas) + ")\\..*",
                // exclude history table
                "(.*\\.)?" + defaultSchema + "\\." + context.getConfiguration().getTable()
        );

        lintProcessor.lint(
                context.getConnection(),
                defaultSchema,
                catalog,
                context.getMigrationInfo().getVersion(),
                defaultTablePattern
        );
    }

    @Override
    public String getCallbackName() {
        return getClass().getSimpleName();
    }

}
