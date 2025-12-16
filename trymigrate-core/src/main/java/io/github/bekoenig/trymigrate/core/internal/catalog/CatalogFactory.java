package io.github.bekoenig.trymigrate.core.internal.catalog;

import io.github.bekoenig.trymigrate.core.plugin.customize.TrymigrateCatalogCustomizer;
import org.junit.platform.commons.util.ClassLoaderUtils;
import schemacrawler.schema.Catalog;
import schemacrawler.schemacrawler.*;
import schemacrawler.tools.options.Config;
import schemacrawler.tools.options.ConfigUtility;
import schemacrawler.tools.utility.SchemaCrawlerUtility;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.logging.LogManager;

public class CatalogFactory {

    private static final String LOG_PROPERTIES = "io/github/bekoenig/trymigrate/core/internal/catalog/log.properties";

    static {
        try (InputStream is = ClassLoaderUtils.getClassLoader(CatalogFactory.class)
                .getResourceAsStream(LOG_PROPERTIES)) {
            LogManager.getLogManager().readConfiguration(is);
        } catch (IOException e) {
            throw new IllegalArgumentException("Failed loading log configuration", e);
        }
    }

    private final SchemaCrawlerOptions schemaCrawlerOptions;
    private final Config additionalConfig;

    public CatalogFactory(List<TrymigrateCatalogCustomizer> customizers) {
        LimitOptions limitOptions = customizedBuild(LimitOptionsBuilder.builder()
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

        this.schemaCrawlerOptions = SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions()
                .withLimitOptions(limitOptions)
                .withFilterOptions(filterOptions)
                .withGrepOptions(grepOptions)
                .withLoadOptions(loadOptions);

        additionalConfig = customizedBuild(new HashMap<>(),
                customizers, c -> c::customize, ConfigUtility::fromMap);
    }

    private <B, C, O> O customizedBuild(B builder, List<C> customizers, Function<C, Consumer<B>> customizerAdapter,
                                        Function<B, O> finalizer) {
        customizers.forEach(customizer -> customizerAdapter.apply(customizer).accept(builder));
        return finalizer.apply(builder);
    }

    public Catalog crawl(Connection connection) {
        DatabaseConnectionSourceAdapter dataSource = new DatabaseConnectionSourceAdapter(connection);
        return SchemaCrawlerUtility.getCatalog(dataSource, SchemaCrawlerUtility.matchSchemaRetrievalOptions(dataSource),
                schemaCrawlerOptions, additionalConfig);
    }

}
