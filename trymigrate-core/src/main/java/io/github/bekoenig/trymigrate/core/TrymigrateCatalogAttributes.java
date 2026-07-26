package io.github.bekoenig.trymigrate.core;

/**
 * Attribute keys set by trymigrate on the SchemaCrawler {@code Catalog} object.
 * <p>
 * These attributes provide migration context to linters, reports or exports. They are set after
 * catalog was crawled and can be accessed via {@code catalog.getAttribute(key)}.
 * <p>
 * <b>Example usage:</b>
 * <pre>{@code
 * Catalog catalog = getCatalog();
 * String migrationVersion = catalog.getAttribute(TrymigrateCatalogAttributes.MIGRATION_VERSION);
 * String defaultSchema = catalog.getAttribute(TrymigrateCatalogAttributes.DEFAULT_SCHEMA);
 * }</pre>
 */
public final class TrymigrateCatalogAttributes {

    /**
     * The current Flyway migration version being processed (e.g., "1.0", "2.1").
     * <p>
     * Type: {@code String}
     */
    public static final String MIGRATION_VERSION = "trymigrate.migrationVersion";

    /**
     * The default schema configured for Flyway migrations.
     * <p>
     * Type: {@code String}
     */
    public static final String DEFAULT_SCHEMA = "trymigrate.defaultSchema";

    private TrymigrateCatalogAttributes() {
        // Utility class
    }

}

