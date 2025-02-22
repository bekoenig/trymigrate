package io.github.bekoenig.trymigrate.core.internal.schemacrawler.lint;

import org.junit.jupiter.api.Test;
import schemacrawler.tools.lint.Lint;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class LintPatternsTest {

    @Test
    void matchesAndNotMatches() {
        // GIVEN
        LintPattern lintPattern0 = new LintPattern("a.b.c.SomeLinter", "SCHEMA.TABLE");
        LintPattern lintPattern1 = new LintPattern("a.b.c.SomeOtherLinter", "SCHEMA.TABLE.COL");
        LintPatterns lintPatterns = new LintPatterns(List.of(lintPattern0, lintPattern1));

        Lint<?> lint = mock(Lint.class);
        when(lint.getLinterId()).thenReturn("a.b.c.SomeLinter");
        when(lint.getObjectName()).thenReturn("SCHEMA.TABLE");

        // WHEN
        boolean matches = lintPatterns.matches(lint);
        boolean notMatches = lintPatterns.notMatches(lint);

        // THEN
        assertThat(matches).isTrue();
        assertThat(notMatches).isFalse();
    }

}