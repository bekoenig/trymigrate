package io.github.bekoenig.trymigrate.core.lint.config;

import schemacrawler.tools.lint.LintSeverity;

import java.util.Map;

/**
 * Intermediate interface to add specific configurations to the enabled linter or enable the next linter.
 *
 * @see TrymigrateLintersConfigurer
 * @see TrymigrateLintersConfiguration
 */
public interface TrymigrateLinterConfiguration extends TrymigrateLintersConfiguration {

    /**
     * Adds config properties.
     *
     * @param config config properties as key, value
     * @return intermediate interface
     */
    TrymigrateLinterConfiguration config(Map<String, Object> config);

    /**
     * Overwrites severity.
     *
     * @param severity new severity
     * @return intermediate interface
     */
    TrymigrateLinterConfiguration severity(LintSeverity severity);

}
