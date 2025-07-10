package io.github.bekoenig.trymigrate.core.internal.lint.config;

import io.github.bekoenig.trymigrate.core.lint.config.TrymigrateLinterConfiguration;
import io.github.bekoenig.trymigrate.core.lint.config.TrymigrateLintersConfiguration;
import schemacrawler.tools.lint.LintSeverity;
import schemacrawler.tools.lint.LinterInitializer;
import schemacrawler.tools.lint.Linters;
import schemacrawler.tools.lint.config.LinterConfig;
import schemacrawler.tools.lint.config.LinterConfigs;
import schemacrawler.tools.options.Config;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class LintersBuilder implements TrymigrateLintersConfiguration, TrymigrateLinterConfiguration {

    private final DefaultablePattern tablePattern;
    private final List<LinterConfig> configs = new ArrayList<>();

    private LinterConfigBuilder currentLinterConfigBuilder;

    private LintersBuilder(DefaultablePattern tablePattern) {
        this.tablePattern = tablePattern;
    }

    public static LintersBuilder builder(DefaultablePattern tablePattern) {
        return new LintersBuilder(tablePattern);
    }

    private void startLinterConfig(String linterId) {
        this.currentLinterConfigBuilder = LinterConfigBuilder.builder()
                .linterId(linterId)
                .tableInclusionPattern(tablePattern.includePattern())
                .tableExclusionPattern(tablePattern.excludePattern())
                .runLinter(true);
    }

    private void endLinterConfig() {
        if (Objects.nonNull(currentLinterConfigBuilder)) {
            configs.add(currentLinterConfigBuilder.build());
            currentLinterConfigBuilder = null;
        }
    }

    public TrymigrateLinterConfiguration enable(String linterId) {
        endLinterConfig();
        startLinterConfig(linterId);
        return this;
    }

    @Override
    public TrymigrateLintersConfiguration disable(String linterId) {
        endLinterConfig();
        configs.removeIf(config -> config.getLinterId().equals(linterId));
        return this;
    }

    @Override
    public TrymigrateLinterConfiguration reenable(String linterId) {
        return disable(linterId).enable(linterId);
    }

    @Override
    public TrymigrateLinterConfiguration config(Map<String, Object> config) {
        currentLinterConfigBuilder.config(config);
        return this;
    }

    @Override
    public TrymigrateLinterConfiguration severity(LintSeverity severity) {
        currentLinterConfigBuilder.severity(severity);
        return this;
    }

    @Override
    public TrymigrateLinterConfiguration tableInclusionPattern(String tableInclusionPattern) {
        currentLinterConfigBuilder.tableInclusionPattern(tablePattern.overlayIncludePattern(tableInclusionPattern));
        return this;
    }

    @Override
    public TrymigrateLinterConfiguration tableExclusionPattern(String tableExclusionPattern) {
        currentLinterConfigBuilder.tableExclusionPattern(tablePattern.overlayExcludePattern(tableExclusionPattern));
        return this;
    }

    @Override
    public TrymigrateLinterConfiguration columnInclusionPattern(String columnInclusionPattern) {
        currentLinterConfigBuilder.columnInclusionPattern(columnInclusionPattern);
        return this;
    }

    @Override
    public TrymigrateLinterConfiguration columnExclusionPattern(String columnExclusionPattern) {
        currentLinterConfigBuilder.columnExclusionPattern(columnExclusionPattern);
        return this;
    }

    public Linters build(LinterInitializer linterInitializer) {
        endLinterConfig();

        LinterConfigs linterConfigs = new LinterConfigs(new Config());
        this.configs.forEach(linterConfigs::add);

        Linters linters = new Linters(linterConfigs, false);
        linters.initialize(linterInitializer);
        return linters;
    }
}
