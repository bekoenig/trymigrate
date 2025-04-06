package io.github.bekoenig.trymigrate.core.lint.config;

import schemacrawler.tools.lint.LinterProvider;

import java.util.function.Consumer;

public interface LintersConfiguration {

    LintersConfiguration register(LinterProvider linterProvider);

    LintersConfiguration include(Consumer<LintersConfiguration> configuration);

    LinterConfiguration enable(LinterProvider linterProvider);

    LinterConfiguration enable(String linterId);

}
