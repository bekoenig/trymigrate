package io.github.bekoenig.trymigrate.core.internal.lint;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import schemacrawler.tools.lint.Lint;
import schemacrawler.tools.lint.LintSeverity;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class LintsVerifierTest {

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