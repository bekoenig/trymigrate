package io.github.bekoenig.trymigrate.core.plugin.customize;

import schemacrawler.schema.Catalog;

/**
 * Plugin interface for exporting the final database catalog after all migrations.
 * <p>
 * Implementations are called once at the end of the test lifecycle (during close),
 * allowing you to generate documentation, diagrams, or other artifacts from the
 * final schema state.
 * <p>
 * <b>Built-in Exporters:</b>
 * <ul>
 *     <li><b>OKF (Open Knowledge Framework):</b> Generates Markdown documentation
 *         using SchemaCrawler Scribe. Output is written to {@code target/trymigrate-scribe/{schema}/}.
 *         The base directory can be customized via system property {@code trymigrate.scribe.basedir}.</li>
 * </ul>
 * <p>
 * <b>Custom Use Cases:</b>
 * <ul>
 *     <li>Generate PlantUML or Mermaid diagrams</li>
 *     <li>Export to custom documentation formats</li>
 *     <li>Push schema metadata to a central registry</li>
 * </ul>
 * <p>
 * Register an exporter locally via
 * {@link io.github.bekoenig.trymigrate.core.plugin.TrymigrateRegisterPlugin}, or make it globally discoverable by
 * implementing {@link io.github.bekoenig.trymigrate.core.plugin.TrymigratePlugin} as well.
 *
 * @see io.github.bekoenig.trymigrate.core.TrymigrateCatalogAttributes
 * @since 1.4.2
 */
public interface TrymigrateCatalogExporter {

    /**
     * Exports the catalog.
     * <p>
     * Called once at the end of the test lifecycle with the final catalog state.
     * The output path is determined by the implementation, typically based on
     * the schema name from {@code catalog.getAttribute(TrymigrateCatalogAttributes.DEFAULT_SCHEMA)}.
     *
     * @param catalog the final database catalog after all migrations
     */
    void export(Catalog catalog);

}

