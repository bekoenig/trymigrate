package io.github.bekoenig.trymigrate.core.internal.lint;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import schemacrawler.tools.lint.Lint;
import schemacrawler.tools.lint.LintSeverity;
import schemacrawler.tools.lint.Lints;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class LintsVerifierTest {

    @Test
    @DisplayName("GIVEN no threshold WHEN verifying THEN do nothing")
    void shouldDoNothingIfDisabled() {
        // GIVEN
        LintsVerifier verifier = new LintsVerifier(null);
        Lints lints = mock();

        // WHEN / THEN
        assertThatCode(() -> verifier.verify(lints, mock())).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("GIVEN lints below threshold WHEN verifying THEN do nothing")
    void shouldDoNothingIfBelowThreshold() {
        // GIVEN
        LintsVerifier verifier = new LintsVerifier(LintSeverity.high);
        Lint<String> lint = mock();
        when(lint.getSeverity()).thenReturn(LintSeverity.medium);
        when(lint.getObjectName()).thenReturn("some_table");
        Lints lints = new Lints(List.of(lint));

        // WHEN / THEN
        assertThatCode(() -> verifier.verify(lints, new LintPatterns(List.of()))).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("GIVEN lints above threshold WHEN verifying THEN throw AssertionError")
    void shouldThrowIfAboveThreshold() {
        // GIVEN
        LintsVerifier verifier = new LintsVerifier(LintSeverity.high);
        Lint<String> lint = mock();
        when(lint.getSeverity()).thenReturn(LintSeverity.critical);
        when(lint.getLinterId()).thenReturn("my.linter");
        when(lint.getObjectName()).thenReturn("some_table");
        Lints lints = new Lints(List.of(lint));

        // WHEN / THEN
        assertThatThrownBy(() -> verifier.verify(lints, new LintPatterns(List.of())))
                .isInstanceOf(AssertionError.class)
                .hasMessageContaining("Some lints have exceeded the quality threshold (high)")
                .hasMessageContaining("my.linter");
    }

    @Test
    @DisplayName("GIVEN suppressed lints above threshold WHEN verifying THEN do nothing")
    void shouldIgnoreSuppressedLints() {
        // GIVEN
        LintsVerifier verifier = new LintsVerifier(LintSeverity.high);
        Lint<String> lint = mock();
        when(lint.getSeverity()).thenReturn(LintSeverity.critical);
        when(lint.getLinterId()).thenReturn("suppressed.linter");
        when(lint.getObjectName()).thenReturn("some_table");
        Lints lints = new Lints(List.of(lint));

        LintPatterns patterns = new LintPatterns(List.of(new LintPattern("suppressed.linter", ".*")));

        // WHEN / THEN
        assertThatCode(() -> verifier.verify(lints, patterns)).doesNotThrowAnyException();
    }

    public static Stream<Arguments> hasOrExceedsSeverityArguments() {
        List<LintSeverity> matchingThresholds = List.of(LintSeverity.low, LintSeverity.medium);
        return Stream.of(LintSeverity.values())
                .map(x -> Arguments.of(x, matchingThresholds.contains(x)));
    }

    @ParameterizedTest
    @MethodSource("hasOrExceedsSeverityArguments")
    void hasOrExceedsSeverity(LintSeverity threshold, boolean expected) {
        // GIVEN
        Lint<? extends Serializable> lint = mock();
        when(lint.getSeverity()).thenReturn(LintSeverity.medium);

        // WHEN
        boolean result = LintsVerifier.hasOrExceedsSeverity(lint, threshold);

        // THEN
        assertThat(result).isEqualTo(expected);
    }
}