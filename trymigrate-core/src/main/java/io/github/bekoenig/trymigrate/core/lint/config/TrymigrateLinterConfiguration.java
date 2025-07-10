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
     * Defines config properties.
     *
     * @param config config properties as key, value
     * @return intermediate interface
     */
    TrymigrateLinterConfiguration config(Map<String, Object> config);

    /**
     * Overwrites predefined severity.
     *
     * @param severity new severity
     * @return intermediate interface
     */
    TrymigrateLinterConfiguration severity(LintSeverity severity);

    /**
     * Defines the inclusion pattern for table.
     *
     * @param tableInclusionPattern inclusion pattern for table
     * @return intermediate interface
     */
    TrymigrateLinterConfiguration tableInclusionPattern(String tableInclusionPattern);

    /**
     * Defines the exclusion pattern for table.
     *
     * @param tableExclusionPattern exclusion pattern for table
     * @return intermediate interface
     */
    TrymigrateLinterConfiguration tableExclusionPattern(String tableExclusionPattern);

    /**
     * Defines the inclusion pattern for column.
     *
     * @param columnInclusionPattern inclusion pattern for column
     * @return intermediate interface
     */
    TrymigrateLinterConfiguration columnInclusionPattern(String columnInclusionPattern);

    /**
     * Defines the exclusion pattern for column.
     *
     * @param columnExclusionPattern exclusion pattern for column
     * @return intermediate interface
     */
    TrymigrateLinterConfiguration columnExclusionPattern(String columnExclusionPattern);
}
