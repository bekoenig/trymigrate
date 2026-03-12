package io.github.bekoenig.trymigrate.core.plugin.customize;

import schemacrawler.schemacrawler.*;

import java.util.Map;

/**
 * Customizer for trymigrate's database catalog lookup.
 * <p>
 * This interface is the entry point for configuring how SchemaCrawler crawls your database.
 * It allows you to filter schemas, tables, and columns, or change how much metadata is loaded.
 * <p>
 * <b>Key Use Cases:</b>
 * <ul>
 *     <li><b>Schema Filtering:</b> Only load specific application schemas and exclude system schemas.</li>
 *     <li><b>Performance:</b> Restrict the lookup to certain table types or exclude metadata like views or procedures.</li>
 *     <li><b>Grepping:</b> Filter the catalog based on specific naming patterns.</li>
 * </ul>
 * <p>
 * <b>Example:</b>
 * <pre>{@code
 * @TrymigrateRegisterPlugin
 * private final TrymigrateCatalogCustomizer catalogCustomizer = new TrymigrateCatalogCustomizer() {
 *     @Override
 *     public void customize(LimitOptionsBuilder builder) {
 *         builder.includeSchemas("APP_.*").excludeSchemas("SYS_.*");
 *     }
 * };
 * }</pre>
 *
 * @see schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder
 */
public interface TrymigrateCatalogCustomizer {

    /**
     * Customizes the limit options (e.g., schemas, tables, or row counts to include).
     *
     * @param builder the builder for SchemaCrawler's limit options
     */
    default void customize(LimitOptionsBuilder builder) {
    }

    /**
     * Customizes the filter options (e.g., excluding specific object types).
     *
     * @param builder the builder for SchemaCrawler's filter options
     */
    default void customize(FilterOptionsBuilder builder) {
    }

    /**
     * Customizes the grep options (filtering based on content or names).
     *
     * @param builder the builder for SchemaCrawler's grep options
     */
    default void customize(GrepOptionsBuilder builder) {
    }

    /**
     * Customizes the load options (what metadata levels to load).
     *
     * @param builder the builder for SchemaCrawler's load options
     */
    default void customize(LoadOptionsBuilder builder) {
    }

    /**
     * Provides access to SchemaCrawler's internal configuration map for low-level fine-tuning.
     *
     * @param configMap the internal SchemaCrawler configuration map
     */
    default void customize(Map<String, Object> configMap) {
    }

}
