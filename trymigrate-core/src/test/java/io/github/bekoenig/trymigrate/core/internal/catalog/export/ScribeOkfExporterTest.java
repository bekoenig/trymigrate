package io.github.bekoenig.trymigrate.core.internal.catalog.export;

import io.github.bekoenig.trymigrate.core.plugin.TrymigratePlugin;
import io.github.bekoenig.trymigrate.core.plugin.customize.TrymigrateCatalogExporter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ScribeOkfExporterTest {

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

