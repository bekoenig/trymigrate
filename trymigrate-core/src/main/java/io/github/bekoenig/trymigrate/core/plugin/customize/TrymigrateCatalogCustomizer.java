package io.github.bekoenig.trymigrate.core.plugin.customize;

import schemacrawler.schemacrawler.*;

import java.util.Map;

/**
 * Customizer for trymigrate's database catalog lookup.
 * <p>
 * This interface is the entry point for configuring how SchemaCrawler crawls your database.
 * It allows you to filter schemas, tables, and columns, or change how much metadata is loaded.
 * <p>
 * By default, trymigrate uses Flyway's schema configuration (e.g., {@code flyway.defaultSchema} or
 * {@code flyway.schemas}) to determine which schemas to crawl. You can provide custom implementations
 * of this interface to override or refine this behavior.
 * <p>
 * <b>Key Use Cases:</b>
 * <ul>
 *     <li><b>Schema Filtering:</b> Only load specific application schemas and exclude system schemas.</li>
 *     <li><b>Performance:</b> Restrict the lookup to certain table types or exclude metadata like views or
 *     procedures.</li>
 *     <li><b>Grepping:</b> Filter the catalog based on specific naming patterns.</li>
 * </ul>
 * <p>
 * <b>Example:</b>
 * <pre>{@code
 * @TrymigrateRegisterPlugin
 * private final TrymigrateCatalogCustomizer catalogCustomizer = new TrymigrateCatalogCustomizer() {
 *     @Override
 *     public void customize(LimitOptionsBuilder builder) {
 *         // Override default schema selection and include only schemas starting with "APP_"
 *         // and exclude those starting with "SYS_" using SchemaCrawler's RegularExpressionRule.
 *         builder.includeSchemas(new RegularExpressionRule("APP_.*", "SYS_.*"));
 *     }
 * };
 * }</pre>
 *
 * @see schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder
 */
public interface TrymigrateCatalogCustomizer {

    /**
     * Customizes the limit options (e.g., schemas, tables, or row counts to include).
     * This method is called during the catalog crawling process.
     *
     * @param builder the builder for SchemaCrawler's limit options, allowing you to specify
     *                which schemas, tables, etc., to include or exclude
     */
    default void customize(LimitOptionsBuilder builder) {
    }

    /**
     * Customizes the filter options (e.g., excluding specific object types like views or stored procedures).
     * This method allows for more granular filtering beyond schema and table inclusion/exclusion.
     *
     * @param builder the builder for SchemaCrawler's filter options
     */
    default void customize(FilterOptionsBuilder builder) {
    }

    /**
     * Customizes the grep options, which allow filtering based on names or content patterns.
     *
     * @param builder the builder for SchemaCrawler's grep options
     */
    default void customize(GrepOptionsBuilder builder) {
    }

    /**
     * Customizes the load options, determining what levels of metadata are loaded (e.g., tables, columns, indexes,
     * remarks).
     *
     * @param builder the builder for SchemaCrawler's load options
     */
    default void customize(LoadOptionsBuilder builder) {
    }

    /**
     * Provides access to SchemaCrawler's internal configuration map for low-level fine-tuning.
     * Use this for advanced configurations not covered by other customize methods.
     *
     * @param configMap the internal SchemaCrawler configuration map
     */
    default void customize(Map<String, Object> configMap) {
    }

}
