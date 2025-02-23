package io.github.bekoenig.trymigrate.core.lint.config;

import schemacrawler.tools.lint.LintSeverity;

import java.util.Map;

public interface LinterConfiguration extends LintersConfiguration {

    LinterConfiguration config(Map<String, Object> config);

    LinterConfiguration severity(LintSeverity severity);

    LinterConfiguration threshold(int threshold);

}
