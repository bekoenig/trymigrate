package io.github.bekoenig.trymigrate.core.internal.lint.report;

import org.flywaydb.core.api.MigrationVersion;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junitpioneer.jupiter.ClearSystemProperty;

import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class LintsHtmlReporterTest {

    private final LintsHtmlReporter resolver = new LintsHtmlReporter();

    @Test
    @DisplayName("GIVEN no base directory property WHEN resolving path THEN return path in default target directory")
    void resolve_defaultBaseDir() {
        // GIVEN
        String schema = "MY_SCHEMA";
        MigrationVersion migrationVersion = MigrationVersion.fromVersion("1.0");

        // WHEN
        Path path = resolver.resolve(schema, migrationVersion);

        // THEN
        assertThat(path).endsWithRaw(Path.of("target", "trymigrate-lint-reports", "MY_SCHEMA", "1_0.html"));
    }

    @Test
    @DisplayName("GIVEN a custom base directory property WHEN resolving path THEN return path in specified directory")
    @ClearSystemProperty(key = LintsHtmlReporter.PROPERTY_NAME)
    void resolve_propertyBaseDir(@TempDir Path tempDir) {
        System.setProperty(LintsHtmlReporter.PROPERTY_NAME, tempDir.toString());

        // GIVEN
        String schema = "MY_SCHEMA";
        MigrationVersion migrationVersion = MigrationVersion.fromVersion("1.0");

        // WHEN
        Path path = resolver.resolve(schema, migrationVersion);

        // THEN
        assertThat(path).startsWithRaw(tempDir);
        assertThat(path).endsWithRaw(Path.of("trymigrate-lint-reports", "MY_SCHEMA", "1_0.html"));
    }

}
