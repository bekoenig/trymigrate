package io.github.bekoenig.trymigrate.core.internal.jupiter;

import io.github.bekoenig.trymigrate.core.Trymigrate;
import io.github.bekoenig.trymigrate.core.plugin.TrymigrateBeanProvider;
import io.github.bekoenig.trymigrate.core.internal.flyway.FlywayConfigurationFactory;
import io.github.bekoenig.trymigrate.core.internal.schemacrawler.lint.LintsAssert;
import io.github.bekoenig.trymigrate.core.internal.schemacrawler.lint.LintsHistory;
import org.flywaydb.core.api.MigrationVersion;
import org.junit.jupiter.api.extension.ExtensionContext;
import schemacrawler.schema.Catalog;
import schemacrawler.tools.lint.Lints;

import javax.sql.DataSource;

public class StoreSupport {

    private static final String BEAN_PROVIDER = "bean-provider";
    private static final String DATA_SOURCE = "data-source";
    private static final String CATALOG = "catalog";
    private static final String FLUENT_CONFIGURATION_FACTORY = "fluent-configuration-factory";
    private static final String MIGRATION_VERSION = "migration-version";
    private static final String LINTS_HISTORY = "lints-history";
    private static final String LINTS = "lints";
    private static final String LINTS_ASSERT = "lints-assert";

    private StoreSupport() {
    }

    private static ExtensionContext.Store getStore(ExtensionContext extensionContext) {
        return extensionContext.getStore(ExtensionContext.Namespace.create(Trymigrate.class,
                extensionContext.getRequiredTestClass()));
    }

    public static TrymigrateBeanProvider getBeanProvider(ExtensionContext extensionContext) {
        return getStore(extensionContext).get(BEAN_PROVIDER, TrymigrateBeanProvider.class);
    }

    public static void putBeanProvider(ExtensionContext extensionContext, TrymigrateBeanProvider beanProvider) {
        getStore(extensionContext).put(BEAN_PROVIDER, beanProvider);
    }

    public static DataSource getDataSource(ExtensionContext extensionContext) {
        return getStore(extensionContext).get(DATA_SOURCE, DataSource.class);
    }

    public static void putDataSource(ExtensionContext extensionContext, DataSource dataSource) {
        getStore(extensionContext).put(DATA_SOURCE, dataSource);
    }

    public static Catalog getCatalog(ExtensionContext extensionContext) {
        return getStore(extensionContext).get(CATALOG, Catalog.class);
    }

    public static void putCatalog(ExtensionContext extensionContext, Catalog catalog) {
        getStore(extensionContext).put(CATALOG, catalog);
    }

    public static FlywayConfigurationFactory getFlywayConfigurationFactory(ExtensionContext extensionContext) {
        return getStore(extensionContext).get(FLUENT_CONFIGURATION_FACTORY, FlywayConfigurationFactory.class);
    }

    public static void putFlywayConfigurationFactory(ExtensionContext extensionContext,
                                                     FlywayConfigurationFactory flywayConfigurationFactory) {
        getStore(extensionContext).put(FLUENT_CONFIGURATION_FACTORY, flywayConfigurationFactory);
    }

    public static MigrationVersion getMigrationVersion(ExtensionContext extensionContext) {
        return getStore(extensionContext).get(MIGRATION_VERSION, MigrationVersion.class);
    }

    public static void putMigrationVersion(ExtensionContext extensionContext, MigrationVersion migrationVersion) {
        getStore(extensionContext).put(MIGRATION_VERSION, migrationVersion);
    }

    public static LintsHistory getLintsHistory(ExtensionContext extensionContext) {
        return getStore(extensionContext).get(LINTS_HISTORY, LintsHistory.class);
    }

    public static void putLintsHistory(ExtensionContext extensionContext, LintsHistory lintsHistory) {
        getStore(extensionContext).put(LINTS_HISTORY, lintsHistory);
    }

    public static Lints getLints(ExtensionContext extensionContext) {
        return getStore(extensionContext).get(LINTS, Lints.class);
    }

    public static void putLints(ExtensionContext extensionContext, Lints lints) {
        getStore(extensionContext).put(LINTS, lints);
    }

    public static LintsAssert getLintsAssert(ExtensionContext extensionContext) {
        return getStore(extensionContext).get(LINTS_ASSERT, LintsAssert.class);
    }

    public static void putLintsAssert(ExtensionContext extensionContext, LintsAssert lintsAssert) {
        getStore(extensionContext).put(LINTS_ASSERT, lintsAssert);
    }

}
