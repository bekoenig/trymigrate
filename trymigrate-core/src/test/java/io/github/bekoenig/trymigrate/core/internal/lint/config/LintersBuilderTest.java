package io.github.bekoenig.trymigrate.core.internal.lint.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import schemacrawler.tools.lint.LintSeverity;
import schemacrawler.tools.lint.LinterProvider;
import schemacrawler.tools.lint.Linters;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class LintersBuilderTest {

    @Test
    @DisplayName("GIVEN a builder WHEN configuring linter THEN linters instance contains config")
    void shouldBuildLintersWithConfiguration() {
        // GIVEN
        RestrictedPattern pattern = new RestrictedPattern("include.*", "exclude.*");
        LintersBuilder builder = LintersBuilder.builder(pattern);

        // WHEN
        builder
                .configure("linter1")
                .severity(LintSeverity.high)
                .configure("linter2")
                .tableInclusionPattern("custom.*");

        Linters linters = builder.build();

        // THEN
        assertThat(linters).isNotNull();
    }

    @Test
    @DisplayName("GIVEN a builder WHEN disabling linter THEN it is removed")
    void shouldDisableLinter() {
        // GIVEN
        LintersBuilder builder = LintersBuilder.builder(new RestrictedPattern(".*", ""));
        builder.configure("linter1");

        // WHEN
        builder.disable("linter1");
        Linters linters = builder.build();

        // THEN
        assertThat(linters).isNotNull();
    }

    @Test
    @DisplayName("GIVEN a builder WHEN registering provider THEN provider is added")
    void shouldRegisterProvider() {
        // GIVEN
        LintersBuilder builder = LintersBuilder.builder(new RestrictedPattern(".*", ""));
        LinterProvider provider = mock();

        // WHEN
        builder.register(provider);
        Linters linters = builder.build();

        // THEN
        assertThat(linters).isNotNull();
    }

}
