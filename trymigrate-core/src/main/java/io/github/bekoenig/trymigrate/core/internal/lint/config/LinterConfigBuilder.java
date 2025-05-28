package io.github.bekoenig.trymigrate.core.internal.lint.config;

import schemacrawler.tools.lint.LintSeverity;
import schemacrawler.tools.lint.config.LinterConfig;

import java.util.Map;

public class LinterConfigBuilder {

    private String linterId;
    private Map<String, Object> config;
    private boolean runLinter;
    private LintSeverity severity;
    private int threshold;
    private String tableInclusionPattern;
    private String tableExclusionPattern;
    private String columnInclusionPattern;
    private String columnExclusionPattern;

    private LinterConfigBuilder() {
    }

    public static LinterConfigBuilder builder() {
        return new LinterConfigBuilder();
    }

    public LinterConfigBuilder linterId(String linterId) {
        this.linterId = linterId;
        return this;
    }

    public LinterConfigBuilder config(Map<String, Object> config) {
        this.config = config;
        return this;
    }

    public LinterConfigBuilder runLinter(boolean runLinter) {
        this.runLinter = runLinter;
        return this;
    }

    public LinterConfigBuilder severity(LintSeverity severity) {
        this.severity = severity;
        return this;
    }

    public LinterConfigBuilder threshold(int threshold) {
        this.threshold = threshold;
        return this;
    }

    public LinterConfigBuilder tableInclusionPattern(String tableInclusionPattern) {
        this.tableInclusionPattern = tableInclusionPattern;
        return this;
    }

    public LinterConfigBuilder tableExclusionPattern(String tableExclusionPattern) {
        this.tableExclusionPattern = tableExclusionPattern;
        return this;
    }

    public LinterConfigBuilder columnInclusionPattern(String columnInclusionPattern) {
        this.columnInclusionPattern = columnInclusionPattern;
        return this;
    }

    public LinterConfigBuilder columnExclusionPattern(String columnExclusionPattern) {
        this.columnExclusionPattern = columnExclusionPattern;
        return this;
    }

    public LinterConfig build() {
        return new LinterConfig(
                linterId,
                runLinter,
                severity,
                threshold,
                tableInclusionPattern,
                tableExclusionPattern,
                columnInclusionPattern,
                columnExclusionPattern,
                config
        );
    }
}
