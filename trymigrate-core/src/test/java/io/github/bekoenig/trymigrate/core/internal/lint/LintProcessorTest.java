package io.github.bekoenig.trymigrate.core.internal.lint;

import io.github.bekoenig.trymigrate.core.internal.lint.config.RestrictedPattern;
import io.github.bekoenig.trymigrate.core.plugin.customize.TrymigrateLintersConfigurer;
import io.github.bekoenig.trymigrate.core.plugin.customize.TrymigrateLintsReporter;
import org.flywaydb.core.api.MigrationVersion;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import schemacrawler.schema.Catalog;
import schemacrawler.tools.command.lint.options.LintOptions;
import schemacrawler.tools.lint.Lints;

import java.sql.Connection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class LintProcessorTest {

    private final LintPatterns excludedPatterns = mock();
    private final TrymigrateLintersConfigurer configurer = mock();
    private final LintsHistory history = mock();
    private final TrymigrateLintsReporter reporter = mock();
    private final LintOptions options = mock();
    private final LintsVerifier verifier = mock();

    private final LintProcessor processor = new LintProcessor(
            excludedPatterns,
            List.of(configurer),
            history,
            List.of(reporter),
            options,
            verifier
    );

    @Test
    @DisplayName("GIVEN a version WHEN checking analysis status THEN delegate to history")
    void shouldCheckIfAnalysed() {
        // GIVEN
        MigrationVersion version = MigrationVersion.fromVersion("1.0");
        when(history.isAnalysed(version)).thenReturn(true);

        // WHEN
        boolean result = processor.isAnalysed(version);

        // THEN
        assertThat(result).isTrue();
        verify(history).isAnalysed(version);
    }

    @Test
    @DisplayName("GIVEN versions and patterns WHEN verifying THEN delegate to verifier using diff")
    void shouldVerifyLints() {
        // GIVEN
        MigrationVersion last = MigrationVersion.fromVersion("1.0");
        MigrationVersion current = MigrationVersion.fromVersion("1.1");
        LintPatterns suppressed = mock();
        Lints diff = mock();
        when(history.diffLints(last, current)).thenReturn(diff);

        // WHEN
        processor.verify(last, current, suppressed);

        // THEN
        verify(verifier).verify(diff, suppressed);
    }

    @Test
    @DisplayName("GIVEN lints WHEN linting THEN process and report new lints")
    void shouldLintAndReport() throws Exception {
        // GIVEN
        Connection connection = mock();
        when(connection.isValid(anyInt())).thenReturn(true);
        Catalog catalog = mock();
        MigrationVersion version = MigrationVersion.fromVersion("1.0");
        RestrictedPattern pattern = new RestrictedPattern(".*", "");

        when(excludedPatterns.dropMatching(any())).thenAnswer(i -> i.getArgument(0));
        when(history.diffNewLints(eq(version), any())).thenAnswer(i -> i.getArgument(1));

        // WHEN
        processor.lint(connection, "schema", catalog, version, pattern);

        // THEN
        verify(configurer).accept(any());
        verify(reporter).report(eq(catalog), any(), eq("schema"), eq(version), eq(options));
    }

    @Test
    @DisplayName("GIVEN a version WHEN getting lints THEN delegate to history")
    void shouldGetLints() {
        // GIVEN
        MigrationVersion version = MigrationVersion.fromVersion("1.0");
        Lints lints = mock();
        when(history.getLints(version)).thenReturn(lints);

        // WHEN
        Lints result = processor.getLints(version);

        // THEN
        assertThat(result).isSameAs(lints);
    }
}
