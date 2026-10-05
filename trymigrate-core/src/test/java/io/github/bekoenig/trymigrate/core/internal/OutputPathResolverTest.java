package io.github.bekoenig.trymigrate.core.internal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junitpioneer.jupiter.ClearSystemProperty;
import org.junitpioneer.jupiter.SetSystemProperty;

import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class OutputPathResolverTest {

    private static final String TEST_PROPERTY = "test.output.basedir";

    @Test
    @DisplayName("GIVEN schema WHEN resolve THEN returns path with subfolder and schema")
    void resolve_withSchema() {
        // GIVEN
        OutputPathResolver resolver = new OutputPathResolver(null, "test-output");
        String schema = "MY_SCHEMA";

        // WHEN
        Path path = resolver.resolve(schema);

        // THEN
        assertThat(path).isNotNull();
        assertThat(path.toString()).endsWith("test-output/MY_SCHEMA");
        assertThat(path).exists();
    }

    @Test
    @DisplayName("GIVEN null schema WHEN resolve THEN uses fallback name")
    void resolve_withNullSchema() {
        // GIVEN
        OutputPathResolver resolver = new OutputPathResolver(null, "test-output");

        // WHEN
        Path path = resolver.resolve(null);

        // THEN
        assertThat(path.toString()).endsWith("test-output/schema-undefined");
    }

    @Test
    @DisplayName("GIVEN schema and filename WHEN resolve THEN returns file path")
    void resolve_withSchemaAndFilename() {
        // GIVEN
        OutputPathResolver resolver = new OutputPathResolver(null, "test-output");
        String schema = "MY_SCHEMA";
        String filename = "report.html";

        // WHEN
        Path path = resolver.resolve(schema, filename);

        // THEN
        assertThat(path.toString()).endsWith("test-output/MY_SCHEMA/report.html");
    }

    @Test
    @DisplayName("GIVEN schema and segments WHEN resolveNested THEN appends segments after schema")
    void resolveNested_withSegments() {
        // GIVEN
        OutputPathResolver resolver = new OutputPathResolver(null, "test-output");
        String schema = "MY_SCHEMA";

        // WHEN
        Path path = resolver.resolveNested(schema, "1.1");

        // THEN
        assertThat(path.toString()).endsWith("test-output/MY_SCHEMA/1.1");
        assertThat(path).exists();
    }

    @Test
    @DisplayName("GIVEN multiple segments WHEN resolveNested THEN appends all segments in order")
    void resolveNested_withMultipleSegments() {
        // GIVEN
        OutputPathResolver resolver = new OutputPathResolver(null, "test-output");

        // WHEN
        Path path = resolver.resolveNested("MY_SCHEMA", "1.1", "sub");

        // THEN
        assertThat(path.toString()).endsWith("test-output/MY_SCHEMA/1.1/sub");
    }

    @Test
    @DisplayName("GIVEN no segments WHEN resolveNested THEN behaves like resolve")
    void resolveNested_withoutSegments() {
        // GIVEN
        OutputPathResolver resolver = new OutputPathResolver(null, "test-output");

        // WHEN
        Path path = resolver.resolveNested("MY_SCHEMA");

        // THEN
        assertThat(path.toString()).endsWith("test-output/MY_SCHEMA");
    }

    @Test
    @DisplayName("GIVEN null segment WHEN resolveNested THEN uses fallback for that segment")
    void resolveNested_withNullSegment() {
        // GIVEN
        OutputPathResolver resolver = new OutputPathResolver(null, "test-output");

        // WHEN
        Path path = resolver.resolveNested("MY_SCHEMA", (String) null);

        // THEN
        assertThat(path.toString()).endsWith("test-output/MY_SCHEMA/segment-undefined");
    }

    @Test
    @SetSystemProperty(key = TEST_PROPERTY, value = "/tmp/custom-basedir")
    @DisplayName("GIVEN custom basedir property WHEN resolve THEN uses custom basedir")
    void resolve_withCustomBasedir(@TempDir Path tempDir) {
        // GIVEN
        System.setProperty(TEST_PROPERTY, tempDir.toString());
        OutputPathResolver resolver = new OutputPathResolver(TEST_PROPERTY, "test-output");
        String schema = "MY_SCHEMA";

        // WHEN
        Path path = resolver.resolve(schema);

        // THEN
        assertThat(path.toString()).startsWith(tempDir.toString());
        assertThat(path.toString()).endsWith("test-output/MY_SCHEMA");
    }

    @Test
    @ClearSystemProperty(key = TEST_PROPERTY)
    @DisplayName("GIVEN no basedir property WHEN resolve THEN uses target folder")
    void resolve_withoutBasedirProperty() {
        // GIVEN
        OutputPathResolver resolver = new OutputPathResolver(TEST_PROPERTY, "test-output");
        String schema = "MY_SCHEMA";

        // WHEN
        Path path = resolver.resolve(schema);

        // THEN
        // Should use target folder (parent of test-classes)
        assertThat(path).isNotNull();
        assertThat(path.toString()).contains("test-output");
    }

}

