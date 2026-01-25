package io.github.bekoenig.trymigrate.core.internal.catalog;

import io.github.bekoenig.trymigrate.core.plugin.customize.TrymigrateCatalogCustomizer;
import schemacrawler.inclusionrule.RegularExpressionRule;
import schemacrawler.schema.Catalog;
import schemacrawler.schemacrawler.*;
import schemacrawler.tools.options.Config;
import schemacrawler.tools.options.ConfigUtility;
import schemacrawler.tools.utility.SchemaCrawlerUtility;
import us.fatehi.utility.datasource.DatabaseConnectionSource;
import us.fatehi.utility.datasource.DatabaseConnectionSources;

import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

public class CatalogFactory {

    private final List<TrymigrateCatalogCustomizer> customizers;

    public CatalogFactory(List<TrymigrateCatalogCustomizer> customizers) {
        this.customizers = customizers;
    }

    private <B, C, O> O customizedBuild(B builder, List<C> customizers, Function<C, Consumer<B>> customizerAdapter,
                                        Function<B, O> finalizer) {
        customizers.forEach(customizer -> customizerAdapter.apply(customizer).accept(builder));
        return finalizer.apply(builder);
    }

    public Catalog crawl(Connection connection, Set<String> schemas) {
        LimitOptions limitOptions = customizedBuild(LimitOptionsBuilder.builder()
                        // include all managed schemas
                        .includeSchemas(new RegularExpressionRule(
                                "(.*\\.)?(" + String.join("|", schemas) + ")", null))
                        .includeAllRoutines()
                        .includeAllSequences()
                        .includeAllSynonyms(),
                customizers, c -> c::customize, OptionsBuilder::build);

        FilterOptions filterOptions = customizedBuild(FilterOptionsBuilder.builder(),
                customizers, c -> c::customize, OptionsBuilder::build);

        GrepOptions grepOptions = customizedBuild(GrepOptionsBuilder.builder(),
                customizers, c -> c::customize, OptionsBuilder::build);

        LoadOptions loadOptions = customizedBuild(
                LoadOptionsBuilder.builder().withSchemaInfoLevel(SchemaInfoLevelBuilder.maximum()),
                customizers, c -> c::customize, OptionsBuilder::build);

        SchemaCrawlerOptions schemaCrawlerOptions = SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions()
                .withLimitOptions(limitOptions)
                .withFilterOptions(filterOptions)
                .withGrepOptions(grepOptions)
                .withLoadOptions(loadOptions);

        Config additionalConfig = customizedBuild(new HashMap<>(),
                customizers, c -> c::customize, ConfigUtility::fromMap);

        DatabaseConnectionSource dataSource = DatabaseConnectionSources.fromConnection(connection);
        return SchemaCrawlerUtility.getCatalog(dataSource, SchemaCrawlerUtility.matchSchemaRetrievalOptions(dataSource),
                schemaCrawlerOptions, additionalConfig);
    }

}
