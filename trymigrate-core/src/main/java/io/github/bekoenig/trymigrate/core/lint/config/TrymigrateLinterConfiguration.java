package io.github.bekoenig.trymigrate.core.lint.config;

import schemacrawler.tools.lint.LintSeverity;

import java.util.Map;

public interface TrymigrateLinterConfiguration extends TrymigrateLintersConfiguration {

    TrymigrateLinterConfiguration config(Map<String, Object> config);

    TrymigrateLinterConfiguration severity(LintSeverity severity);

    TrymigrateLinterConfiguration threshold(int threshold);

}
