package io.github.bekoenig.trymigrate.core.internal.schemacrawler.lint.config;

import io.github.bekoenig.trymigrate.core.lint.config.LinterConfiguration;
import io.github.bekoenig.trymigrate.core.lint.config.LintersConfiguration;
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
import java.util.function.Function;

public class LintersBuilder implements LintersConfiguration, LinterConfiguration {

    private final List<LinterConfig> configs = new ArrayList<>();

    private final Function<String, LinterConfigBuilder> linterConfigBuilderFactory;

    private LinterConfigBuilder currentLinterConfigBuilder;

    private LintersBuilder(Function<String, LinterConfigBuilder> linterConfigBuilderFactory) {
        this.linterConfigBuilderFactory = linterConfigBuilderFactory;
    }

    public static LintersBuilder builder(Function<String, LinterConfigBuilder> linterConfigBuilderFactory) {
        return new LintersBuilder(linterConfigBuilderFactory);
    }

    private void startLinterConfig(String linterId) {
        this.currentLinterConfigBuilder = linterConfigBuilderFactory.apply(linterId);
    }

    private void endLinterConfig() {
        if (Objects.nonNull(currentLinterConfigBuilder)) {
            configs.add(currentLinterConfigBuilder.build());
            currentLinterConfigBuilder = null;
        }
    }

    public LinterConfiguration enable(String linterId) {
        endLinterConfig();
        startLinterConfig(linterId);
        return this;
    }

    @Override
    public LinterConfiguration config(Map<String, Object> config) {
        currentLinterConfigBuilder = currentLinterConfigBuilder.config(config);
        return this;
    }

    @Override
    public LinterConfiguration severity(LintSeverity severity) {
        currentLinterConfigBuilder = currentLinterConfigBuilder.severity(severity);
        return this;
    }

    @Override
    public LinterConfiguration threshold(int threshold) {
        currentLinterConfigBuilder = currentLinterConfigBuilder.threshold(threshold);
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
