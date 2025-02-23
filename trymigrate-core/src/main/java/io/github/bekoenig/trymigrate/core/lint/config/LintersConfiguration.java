package io.github.bekoenig.trymigrate.core.lint.config;

import schemacrawler.tools.lint.LinterProvider;

import java.util.function.Consumer;

public interface LintersConfiguration {

    LintersConfiguration register(LinterProvider linterProvider);

    LintersConfiguration removeAllConfigs(String linterId);

    LintersConfiguration merge(Consumer<LintersConfiguration> configuration);

    LinterConfiguration addConfig(LinterProvider linterProvider);

    LinterConfiguration addConfig(String linterId);

    LinterConfiguration replaceAll(String linterId);
}
