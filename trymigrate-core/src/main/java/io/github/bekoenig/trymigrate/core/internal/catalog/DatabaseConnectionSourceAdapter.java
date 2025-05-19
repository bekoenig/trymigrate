package io.github.bekoenig.trymigrate.core.internal.catalog;

import org.junit.platform.commons.util.ClassLoaderUtils;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import us.fatehi.utility.datasource.DatabaseConnectionSource;

import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.util.function.Consumer;

/**
 * Simple adapter to use an existing {@link Connection} as {@link DatabaseConnectionSource} for
 * {@link schemacrawler.tools.utility.SchemaCrawlerUtility#getCatalog(DatabaseConnectionSource, SchemaCrawlerOptions)}.
 * <p>
 * Supplies a {@link Proxy} instead of the original connection to prevent calls to {@link Connection#close()}.
 */
public class DatabaseConnectionSourceAdapter implements DatabaseConnectionSource {

    private final Connection connection;

    public DatabaseConnectionSourceAdapter(Connection connection) {
        this.connection = connection;
    }

    protected static Connection closePreventingProxy(Connection connection) {
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
    public Connection get() {
        return closePreventingProxy(connection);
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

}
