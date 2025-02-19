package io.github.bekoenig.trymigrate.core.internal.schemacrawler.catalog;

import org.junit.platform.commons.util.ClassLoaderUtils;
import schemacrawler.schema.Catalog;
import schemacrawler.schemacrawler.*;
import schemacrawler.tools.utility.SchemaCrawlerUtility;
import us.fatehi.utility.datasource.DatabaseConnectionSource;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.LogManager;

public class CatalogFactory {

    private static final String LOG_PROPERTIES =
            "io/github/bekoenig/trymigrate/core/internal/schemacrawler/catalog/log.properties";

    static {
        try (InputStream is = ClassLoaderUtils.getClassLoader(CatalogFactory.class).getResourceAsStream(LOG_PROPERTIES)) {
            LogManager.getLogManager().readConfiguration(is);
        } catch (IOException e) {
            throw new IllegalArgumentException("Failed loading log configuration", e);
        }
    }

    private CatalogFactory() {
    }

    public static Catalog crawl(Connection connection, List<String> schemas) {
        return SchemaCrawlerUtility.getCatalog(wrap(connection),
                SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions()
                        .withLimitOptions(LimitOptionsBuilder.builder()
                                .includeSchemas(schemas::contains)
                                .build())
                        .withLoadOptions(LoadOptionsBuilder.builder()
                                .withSchemaInfoLevel(SchemaInfoLevelBuilder.builder()
                                        .withInfoLevel(InfoLevel.maximum)
                                        // ignore database instance and server details
                                        .setRetrieveAdditionalJdbcDriverInfo(false)
                                        .setRetrieveAdditionalDatabaseInfo(false)
                                        .setRetrieveDatabaseInfo(false)
                                        .setRetrieveDatabaseUsers(false)
                                        .setRetrieveServerInfo(false)
                                        // ignore privileges
                                        .setRetrieveTablePrivileges(false)
                                        .setRetrieveTableColumnPrivileges(false)
                                        .toOptions())
                                .toOptions()));
    }

    private static DatabaseConnectionSource wrap(Connection connection) {
        return new DatabaseConnectionSource() {
            @Override
            public Connection get() {
                return (Connection) Proxy.newProxyInstance(
                        ClassLoaderUtils.getClassLoader(Connection.class),
                        new Class<?>[]{Connection.class},
                        (proxy, method, args) -> {
                            if (method.getName().equals("close")) {
                                return null;
                            }

                            return method.invoke(connection, args);
                        });
            }

            @Override
            public void close() {
                // Noop
            }

            @Override
            public boolean releaseConnection(Connection connection) {
                // Noop
                return true;
            }

            @Override
            public void setFirstConnectionInitializer(Consumer<Connection> connectionInitializer) {
                // Noop
            }
        };
    }

}
