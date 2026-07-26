package io.github.bekoenig.trymigrate.core;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TrymigrateCatalogAttributesTest {

    @Test
    @DisplayName("GIVEN MIGRATION_VERSION constant WHEN checking value THEN has expected key")
    void migrationVersionConstant() {
        assertThat(TrymigrateCatalogAttributes.MIGRATION_VERSION)
                .isEqualTo("trymigrate.migrationVersion");
    }

    @Test
    @DisplayName("GIVEN DEFAULT_SCHEMA constant WHEN checking value THEN has expected key")
    void defaultSchemaConstant() {
        assertThat(TrymigrateCatalogAttributes.DEFAULT_SCHEMA)
                .isEqualTo("trymigrate.defaultSchema");
    }

    @Test
    @DisplayName("GIVEN attribute keys WHEN checking format THEN follow trymigrate namespace convention")
    void attributeKeysFollowNamingConvention() {
        assertThat(TrymigrateCatalogAttributes.MIGRATION_VERSION).startsWith("trymigrate.");
        assertThat(TrymigrateCatalogAttributes.DEFAULT_SCHEMA).startsWith("trymigrate.");
    }

}

