package io.github.bekoenig.trymigrate.core.internal.lint;

import io.github.bekoenig.trymigrate.core.internal.lint.LintPattern;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import schemacrawler.tools.lint.Lint;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class LintPatternTest {

    @CsvSource({
            "a.b.c.SomeLinter, SCHEMA.TABLE, true",
            "a.b.c.SomeLinter, .*, true",
            "a.b.c.*, SCHEMA.TABLE, true",
            "a.b.c.SomeLinter, SCHEMA.*, true",
            "a.b.c.SomeLinter, SCHEMA.TABLE2, false",
            "a.b.c.SomeLinter2, SCHEMA.TABLE, false",
            "a.b.c.SomeLinter, '', false",
            "a.b.c.SomeLinter, SCHEMA2.*, false",
    })
    @ParameterizedTest
    void matches(String linterId, String objectName, boolean expectedMatch) {
        // GIVEN
        LintPattern lintPattern = new LintPattern(linterId, objectName);

        Lint<?> lint = mock(Lint.class);
        when(lint.getLinterId()).thenReturn("a.b.c.SomeLinter");
        when(lint.getObjectName()).thenReturn("SCHEMA.TABLE");

        // WHEN
        boolean matches = lintPattern.matches(lint);

        // THEN
        assertThat(matches).isEqualTo(expectedMatch);
    }
}