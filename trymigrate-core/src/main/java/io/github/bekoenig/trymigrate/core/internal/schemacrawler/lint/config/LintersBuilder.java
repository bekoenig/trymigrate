package io.github.bekoenig.trymigrate.core.internal.schemacrawler.lint.config;

import io.github.bekoenig.trymigrate.core.lint.config.LinterConfiguration;
import io.github.bekoenig.trymigrate.core.lint.config.LintersConfiguration;
import schemacrawler.tools.lint.LintSeverity;
import schemacrawler.tools.lint.LinterProvider;
import schemacrawler.tools.lint.Linters;
import schemacrawler.tools.lint.config.LinterConfig;
import schemacrawler.tools.lint.config.LinterConfigs;
import schemacrawler.tools.options.Config;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

public class LintersBuilder implements LintersConfiguration, LinterConfiguration {

    private final LinterProviderRegistry registry = new LinterProviderRegistry();

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

    @Override
    public LintersConfiguration register(LinterProvider linterProvider) {
        endLinterConfig();
        if (registry.isRegistered(linterProvider.getLinterId())) {
            throw new IllegalStateException("Provider <%s> already registered. Configure using linter id <%s>."
                    .formatted(linterProvider.getClass().getName(), linterProvider.getLinterId()));
        }
        registry.register(linterProvider);
        return this;
    }

    @Override
    public LintersConfiguration removeAllConfigs(String linterId) {
        endLinterConfig();
        if (this.configs.stream().noneMatch(x -> x.getLinterId().equals(linterId))) {
            throw new IllegalStateException("Linter with id <%s> not configured. Remove unnecessary method call."
                    .formatted(linterId));
        }
        this.configs.removeIf(linterConfig -> linterConfig.getLinterId().equals(linterId));
        return this;
    }

    @Override
    public LintersConfiguration merge(Consumer<LintersConfiguration> configuration) {
        endLinterConfig();
        configuration.accept(this);
        return this;
    }

    @Override
    public LinterConfiguration addConfig(LinterProvider linterProvider) {
        endLinterConfig();
        if (registry.isRegistered(linterProvider.getLinterId())) {
            throw new IllegalStateException("Provider <%s> already registered. Configure using linter id <%s>."
                    .formatted(linterProvider.getClass().getName(), linterProvider.getLinterId()));
        }
        registry.register(linterProvider);
        startLinterConfig(linterProvider.getLinterId());
        return this;
    }

    public LinterConfiguration addConfig(String linterId) {
        endLinterConfig();
        if (!registry.isRegistered(linterId)) {
            throw new IllegalStateException(("No provider for linter with id <%s> registered. " +
                    "Configure using provider instance.").formatted(linterId));
        }
        startLinterConfig(linterId);
        return this;
    }

    @Override
    public LinterConfiguration replaceAll(String linterId) {
        return removeAllConfigs(linterId).addConfig(linterId);
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

    public Linters build() {
        endLinterConfig();

        LinterConfigs linterConfigs = new LinterConfigs(new Config());
        this.configs.forEach(linterConfigs::add);

        Linters linters = new Linters(linterConfigs, false);
        linters.initialize(registry);
        return linters;
    }
}
