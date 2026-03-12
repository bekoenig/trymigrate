package io.github.bekoenig.trymigrate.core.plugin.customize;

import schemacrawler.tools.lint.LintSeverity;
import schemacrawler.tools.lint.LinterProvider;

import java.util.Map;
import java.util.function.Consumer;

/**
 * Plugin interface for fluent SchemaCrawler linter configuration.
 * <p>
 * This is the primary entry point for programmatic control over linters. It allows you to
 * enable, disable, and fine-tune linters without needing to create {@code schemacrawler.config.properties}
 * or use Java SPI for every minor change.
 * <p>
 * <b>Key Features:</b>
 * <ul>
 *     <li><b>SPI-less Registration:</b> Directly register custom linter providers.</li>
 *     <li><b>Fine-grained Control:</b> Overwrite severity levels and filter by table or column patterns.</li>
 *     <li><b>Dynamic Configuration:</b> Pass custom key-value pairs to specific linters.</li>
 * </ul>
 * <p>
 * <b>Example:</b>
 * <pre>{@code
 * @TrymigrateRegisterPlugin
 * private final TrymigrateLintersConfigurer linterConfig = config -> config
 *     .register(new MyCustomLinterProvider())
 *     .configure("schemacrawler.tools.linter.LinterTableWithNoRemarks")
 *         .severity(LintSeverity.high)
 *         .tableInclusionPattern("APP_.*")
 *     .disable("schemacrawler.tools.linter.LinterTableSql");
 * }</pre>
 *
 * @see TrymigrateLintersConfigurer.TrymigrateLintersConfiguration
 */
public interface TrymigrateLintersConfigurer extends Consumer<TrymigrateLintersConfigurer.TrymigrateLintersConfiguration> {

    /**
     * Entry point for the fluent linter configuration DSL.
     */
    interface TrymigrateLintersConfiguration {

        /**
         * Registers a new linter provider. This avoids the need for standard Java SPI discovery.
         *
         * @param linterProvider the provider to register
         * @return the configuration root
         */
        TrymigrateLintersConfiguration register(LinterProvider linterProvider);

        /**
         * Enables and starts configuring a specific linter.
         *
         * @param linterId the ID of the linter to configure
         * @return a configuration interface for the specific linter
         */
        TrymigrateLinterConfiguration configure(String linterId);

        /**
         * Completely disables a specific linter.
         *
         * @param linterId the ID of the linter to disable
         * @return the configuration root
         */
        TrymigrateLintersConfiguration disable(String linterId);

        /**
         * Discards any existing configuration for a linter and starts fresh.
         *
         * @param linterId the ID of the linter to reconfigure
         * @return a configuration interface for the specific linter
         */
        TrymigrateLinterConfiguration reconfigure(String linterId);

        /**
         * Fluent DSL for configuring a specific linter instance.
         */
        interface TrymigrateLinterConfiguration extends TrymigrateLintersConfiguration {

            /**
             * Sets custom configuration properties for the linter.
             *
             * @param config a map of configuration keys and values
             * @return this configuration instance
             */
            TrymigrateLinterConfiguration config(Map<String, Object> config);

            /**
             * Overrides the default severity of this linter.
             *
             * @param severity the new severity level
             * @return this configuration instance
             */
            TrymigrateLinterConfiguration severity(LintSeverity severity);

            /**
             * Sets a regular expression to restrict which tables this linter applies to.
             *
             * @param tableInclusionPattern the regex pattern for table inclusion
             * @return this configuration instance
             */
            TrymigrateLinterConfiguration tableInclusionPattern(String tableInclusionPattern);

            /**
             * Sets a regular expression to exclude specific tables from this linter.
             *
             * @param tableExclusionPattern the regex pattern for table exclusion
             * @return this configuration instance
             */
            TrymigrateLinterConfiguration tableExclusionPattern(String tableExclusionPattern);

            /**
             * Sets a regular expression to restrict which columns this linter applies to.
             *
             * @param columnInclusionPattern the regex pattern for column inclusion
             * @return this configuration instance
             */
            TrymigrateLinterConfiguration columnInclusionPattern(String columnInclusionPattern);

            /**
             * Sets a regular expression to exclude specific columns from this linter.
             *
             * @param columnExclusionPattern the regex pattern for column exclusion
             * @return this configuration instance
             */
            TrymigrateLinterConfiguration columnExclusionPattern(String columnExclusionPattern);
        }

    }

}
