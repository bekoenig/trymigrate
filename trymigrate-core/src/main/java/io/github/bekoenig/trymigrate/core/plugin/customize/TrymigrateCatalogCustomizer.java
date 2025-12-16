package io.github.bekoenig.trymigrate.core.plugin.customize;

import schemacrawler.schemacrawler.*;

import java.util.Map;

/**
 * Customizer for lookup {@link schemacrawler.schema.Catalog}.
 *
 * @see SchemaCrawlerOptionsBuilder
 */
public interface TrymigrateCatalogCustomizer {

    default void customize(LimitOptionsBuilder builder) {
    }

    default void customize(FilterOptionsBuilder builder) {
    }

    default void customize(GrepOptionsBuilder builder) {
    }

    default void customize(LoadOptionsBuilder builder) {
    }

    default void customize(Map<String, Object> configMap) {
    }

}
