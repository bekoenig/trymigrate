package io.github.bekoenig.trymigrate.core.internal.catalog;

import org.junit.platform.commons.util.ClassLoaderUtils;
import schemacrawler.schema.Catalog;
import schemacrawler.schemacrawler.LimitOptions;
import schemacrawler.schemacrawler.LoadOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.tools.utility.SchemaCrawlerUtility;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
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

    private final LimitOptions limitOptions;
    private final LoadOptions loadOptions;

    public CatalogFactory(LimitOptions limitOptions, LoadOptions loadOptions) {
        this.limitOptions = limitOptions;
        this.loadOptions = loadOptions;
    }

    public Catalog crawl(Connection connection) {
        return SchemaCrawlerUtility.getCatalog(new DatabaseConnectionSourceAdapter(connection),
                SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions()
                        .withLimitOptions(limitOptions)
                        .withLoadOptions(loadOptions));
    }

}
