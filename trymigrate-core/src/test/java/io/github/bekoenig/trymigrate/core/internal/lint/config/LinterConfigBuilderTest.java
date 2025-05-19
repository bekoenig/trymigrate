package io.github.bekoenig.trymigrate.core.internal.lint.config;

import org.junit.jupiter.api.Test;
import schemacrawler.tools.lint.LintSeverity;
import schemacrawler.tools.lint.config.LinterConfig;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class LinterConfigBuilderTest {

    @Test
    void build() {
        // GIVEN

        // WHEN
        LinterConfig linterConfig = LinterConfigBuilder.builder()
                .linterId("linterId")
                .config(Map.of("key", "value"))
                .runLinter(true)
                .severity(LintSeverity.high)
                .threshold(3)
                .tableInclusionPattern("tableInclusionPattern.*")
                .tableExclusionPattern("tableExclusionPattern.*")
                .columnInclusionPattern("columnInclusionPattern.*")
                .columnExclusionPattern("columnExclusionPattern.*")
                .build();

        // THEN
        assertThat(linterConfig.getLinterId()).isEqualTo("linterId");
        assertThat(linterConfig.getConfig().getStringValue("key", "none")).isEqualTo("value");
        assertThat(linterConfig.isRunLinter()).isTrue();
        assertThat(linterConfig.getSeverity()).isEqualTo(LintSeverity.high);
        assertThat(linterConfig.getThreshold()).isEqualTo(3);
        assertThat(linterConfig.getTableInclusionRule().test("tableInclusionPatternXXX")).isTrue();
        assertThat(linterConfig.getTableInclusionRule().test("tableExclusionPatternXXX")).isFalse();
        assertThat(linterConfig.getColumnInclusionRule().test("columnInclusionPatternXXX")).isTrue();
        assertThat(linterConfig.getColumnInclusionRule().test("columnExclusionPatternXXX")).isFalse();
    }
}