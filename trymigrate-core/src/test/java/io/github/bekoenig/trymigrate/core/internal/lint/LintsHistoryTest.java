package io.github.bekoenig.trymigrate.core.internal.lint;

import org.flywaydb.core.api.MigrationVersion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import schemacrawler.tools.lint.Lint;
import schemacrawler.tools.lint.Lints;

import java.io.Serializable;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class LintsHistoryTest {

    private LintsHistory lintsHistory;

    @BeforeEach
    void setUp() {
        lintsHistory = new LintsHistory(new LintPatterns(List.of(
                new LintPattern("someLinter", "ANY_TAB"),
                new LintPattern("someOtherLinter", "ANY_COL"))));
    }

    @Test
    void isAnalysed_isTrue() {
        // GIVEN
        lintsHistory.put(MigrationVersion.fromVersion("1.0"), new Lints(List.of()));
        lintsHistory.put(MigrationVersion.fromVersion("1.1"), new Lints(List.of()));

        // WHEN
        boolean actual = lintsHistory.isAnalysed(MigrationVersion.fromVersion("1.1"));

        // THEN
        assertThat(actual).isTrue();
    }

    @Test
    void isAnalysed_isFalse() {
        // GIVEN
        lintsHistory.put(MigrationVersion.fromVersion("1.0"), new Lints(List.of()));
        lintsHistory.put(MigrationVersion.fromVersion("1.1"), new Lints(List.of()));

        // WHEN
        boolean actual = lintsHistory.isAnalysed(MigrationVersion.fromVersion("1.2"));

        // THEN
        assertThat(actual).isFalse();
    }

    @Test
    void put() {
        // GIVEN
        Lints lints = new Lints(List.of());

        // WHEN
        lintsHistory.put(MigrationVersion.fromVersion("1.0"), lints);

        // THEN
        assertThat(lintsHistory.getLints(MigrationVersion.fromVersion("1.0"))).isSameAs(lints);
    }

    @Test
    void getLints_contains() {
        // GIVEN
        Lints lints = new Lints(List.of());
        lintsHistory.put(MigrationVersion.fromVersion("1.0"), lints);

        // WHEN
        Lints actual = lintsHistory.getLints(MigrationVersion.fromVersion("1.0"));

        // THEN
        assertThat(actual).isSameAs(lints);
    }

    @Test
    void getLints_doesNotContains() {
        // GIVEN
        Lints lints = new Lints(List.of());
        lintsHistory.put(MigrationVersion.fromVersion("1.0"), lints);

        // WHEN
        Lints actual = lintsHistory.getLints(MigrationVersion.fromVersion("1.1"));

        // THEN
        assertThat(actual).isNull();
    }

    @Test
    void diffLints_oneHit() {
        // GIVEN
        Lint<? extends Serializable> lint = mock();
        when(lint.getLinterId()).thenReturn("anyLinter");
        lintsHistory.put(MigrationVersion.fromVersion("1.0"), new Lints(List.of(lint)));

        // WHEN
        Lints actual = lintsHistory.diffLints(MigrationVersion.EMPTY, MigrationVersion.fromVersion("1.0"));

        // THEN
        assertThat(actual).hasSize(1).containsOnly(lint);
    }

    @Test
    void diffLints_noHit() {
        // GIVEN
        Lint<? extends Serializable> lint = mock();
        when(lint.getLinterId()).thenReturn("anyLinter");
        lintsHistory.put(MigrationVersion.fromVersion("1.0"), new Lints(List.of(lint)));
        lintsHistory.put(MigrationVersion.fromVersion("1.1"), new Lints(List.of(lint)));

        // WHEN
        Lints actual = lintsHistory.diffLints(MigrationVersion.fromVersion("1.0"), MigrationVersion.fromVersion("1.1"));

        // THEN
        assertThat(actual).isEmpty();
    }

    @Test
    void getLastAnalyzedVersion() {
        // GIVEN
        lintsHistory.put(MigrationVersion.fromVersion("1.0"), new Lints(List.of()));
        lintsHistory.put(MigrationVersion.fromVersion("1.1"), new Lints(List.of()));

        // WHEN
        MigrationVersion actual = lintsHistory.getLastAnalyzedVersion();

        // THEN
        assertThat(actual).isEqualTo(MigrationVersion.fromVersion("1.1"));
    }
}