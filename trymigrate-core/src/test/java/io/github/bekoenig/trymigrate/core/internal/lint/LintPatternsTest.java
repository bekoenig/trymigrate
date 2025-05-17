package io.github.bekoenig.trymigrate.core.internal.lint;

import io.github.bekoenig.trymigrate.core.internal.lint.LintPattern;
import io.github.bekoenig.trymigrate.core.internal.lint.LintPatterns;
import org.junit.jupiter.api.Test;
import schemacrawler.tools.lint.Lint;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class LintPatternsTest {

    private static Lint<?> mockLint(String linterId, String objectName) {
        Lint<?> lint0 = mock();
        when(lint0.getLinterId()).thenReturn(linterId);
        when(lint0.getObjectName()).thenReturn(objectName);
        return lint0;
    }

    @Test
    void matchesAndNotMatches() {
        // GIVEN
        LintPattern lintPattern0 = new LintPattern("a.b.c.SomeLinter", "SCHEMA.TABLE");
        LintPattern lintPattern1 = new LintPattern("a.b.c.SomeOtherLinter", "SCHEMA.TABLE.COL");
        LintPatterns lintPatterns = new LintPatterns(List.of(lintPattern0, lintPattern1));

        Lint<?> lint = mockLint("a.b.c.SomeLinter", "SCHEMA.TABLE");

        // WHEN
        boolean matches = lintPatterns.matches(lint);
        boolean notMatches = lintPatterns.notMatches(lint);

        // THEN
        assertThat(matches).isTrue();
        assertThat(notMatches).isFalse();
    }

    @Test
    void dropMatching() {
        // GIVEN
        LintPatterns lintPatterns = new LintPatterns(List.of(
                new LintPattern("a.b.c.SomeLinter", "SCHEMA.TABLE0")));

        Lint<?> lint0 = mockLint("a.b.c.SomeLinter", "SCHEMA.TABLE0");
        Lint<?> lint1 = mockLint("a.b.c.SomeOtherLinter", "SCHEMA.TABLE1");

        Stream<Lint<? extends Serializable>> lints = Stream.of(lint0, lint1);

        // WHEN
        Stream<Lint<? extends Serializable>> result = lintPatterns.dropMatching(lints);

        // THEN
        assertThat(result)
                .doesNotContain(lint0)
                .contains(lint1);
    }
}