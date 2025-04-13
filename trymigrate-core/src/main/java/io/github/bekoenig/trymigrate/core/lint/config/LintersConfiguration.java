package io.github.bekoenig.trymigrate.core.lint.config;

import schemacrawler.tools.lint.LinterProvider;

public interface LintersConfiguration {

    LintersConfiguration register(LinterProvider linterProvider);

    LinterConfiguration enable(LinterProvider linterProvider);

    LinterConfiguration enable(String linterId);

}
