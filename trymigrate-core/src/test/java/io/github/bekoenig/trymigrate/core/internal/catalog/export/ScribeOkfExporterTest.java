package io.github.bekoenig.trymigrate.core.internal.catalog.export;

import io.github.bekoenig.trymigrate.core.TrymigrateCatalogAttributes;
import io.github.bekoenig.trymigrate.core.plugin.TrymigratePlugin;
import io.github.bekoenig.trymigrate.core.plugin.customize.TrymigrateCatalogExporter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junitpioneer.jupiter.ClearSystemProperty;
import org.junitpioneer.jupiter.SetSystemProperty;
import schemacrawler.schema.Catalog;

import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ScribeOkfExporterTest {

    private Catalog catalogWith(String schema, String version) {
        Catalog catalog = mock(Catalog.class);
        when(catalog.getAttribute(TrymigrateCatalogAttributes.DEFAULT_SCHEMA)).thenReturn(schema);
        when(catalog.getAttribute(TrymigrateCatalogAttributes.MIGRATION_VERSION)).thenReturn(version);
        return catalog;
    }

    @Test
    @DisplayName("GIVEN ScribeOkfExporter WHEN checking PROPERTY_VERSIONED THEN has correct value")
    void propertyVersionedConstant() {
        // THEN
        assertThat(ScribeOkfExporter.PROPERTY_VERSIONED).isEqualTo("trymigrate.scribe.versioned");
    }

    @Test
    @ClearSystemProperty(key = ScribeOkfExporter.PROPERTY_VERSIONED)
    @DisplayName("GIVEN default (versioned) WHEN resolveOutputPath THEN path contains version segment")
    void resolveOutputPath_versionedByDefault() {
        // GIVEN
        ScribeOkfExporter exporter = new ScribeOkfExporter();
        Catalog catalog = catalogWith("MY_SCHEMA", "1.1");

        // WHEN
        Path path = exporter.resolveOutputPath(catalog);

        // THEN
        assertThat(path.toString()).endsWith("trymigrate-scribe/MY_SCHEMA/1.1");
    }

    @Test
    @ClearSystemProperty(key = ScribeOkfExporter.PROPERTY_VERSIONED)
    @DisplayName("GIVEN versioned default and missing version WHEN resolveOutputPath THEN uses fallback segment")
    void resolveOutputPath_versionedFallback() {
        // GIVEN
        ScribeOkfExporter exporter = new ScribeOkfExporter();
        Catalog catalog = catalogWith("MY_SCHEMA", null);

        // WHEN
        Path path = exporter.resolveOutputPath(catalog);

        // THEN
        assertThat(path.toString()).endsWith("trymigrate-scribe/MY_SCHEMA/segment-undefined");
    }

    @Test
    @SetSystemProperty(key = ScribeOkfExporter.PROPERTY_VERSIONED, value = "false")
    @DisplayName("GIVEN versioned disabled WHEN resolveOutputPath THEN path has no version segment")
    void resolveOutputPath_versionedDisabled() {
        // GIVEN
        ScribeOkfExporter exporter = new ScribeOkfExporter();
        Catalog catalog = catalogWith("MY_SCHEMA", "1.1");

        // WHEN
        Path path = exporter.resolveOutputPath(catalog);

        // THEN
        assertThat(path.toString()).endsWith("trymigrate-scribe/MY_SCHEMA");
    }

    @Test
    @DisplayName("GIVEN ScribeOkfExporter WHEN checking interfaces THEN implements both TrymigrateCatalogExporter and TrymigratePlugin")
    void implementsRequiredInterfaces() {
        // GIVEN
        ScribeOkfExporter exporter = new ScribeOkfExporter();

        // THEN
        assertThat(exporter).isInstanceOf(TrymigrateCatalogExporter.class);
        assertThat(exporter).isInstanceOf(TrymigratePlugin.class);
    }

    @Test
    @DisplayName("GIVEN ScribeOkfExporter WHEN checking PROPERTY_BASEDIR THEN has correct value")
    void propertyBasedirConstant() {
        // THEN
        assertThat(ScribeOkfExporter.PROPERTY_BASEDIR).isEqualTo("trymigrate.scribe.basedir");
    }


}

