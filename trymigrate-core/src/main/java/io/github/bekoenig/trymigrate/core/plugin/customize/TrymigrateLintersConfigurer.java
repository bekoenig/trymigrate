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
 * By default, trymigrate enables a curated set of SchemaCrawler linters suitable for migration testing.
 * You can provide custom implementations of this interface to
 * configure, disable, or enable specific linters, or to register custom linter providers.
 * <p>
 * <b>Important Notes:</b>
 * <ul>
 *     <li><b>Flyway History:</b> The Flyway schema history table is automatically excluded from linting to prevent
 *     false positives. However, it remains present in the {@code Catalog}.</li>
 *     <li><b>Schema Scope:</b> Linting is strictly limited to schemas managed by Flyway. It is not possible to extend
 *     linting to schemas outside of Flyway's control, even if they are present in the catalog.</li>
 * </ul>
 * <p>
 * <b>Key Features:</b>
 * <ul>
 *     <li><b>SPI-less Registration:</b> Directly register custom linter providers.</li>
 *     <li><b>Fine-grained Control:</b> Overwrite severity levels and filter by table or column patterns.</li>
 *     <li><b>Dynamic Configuration:</b> Pass custom key-value pairs to specific linters.</li>
 * </ul>
 * <p>
 * Register this configurer locally via
 * {@link io.github.bekoenig.trymigrate.core.plugin.TrymigrateRegisterPlugin}, or make it globally discoverable by
 * implementing {@link io.github.bekoenig.trymigrate.core.plugin.TrymigratePlugin} as well.
 * <p>
 * <b>Example:</b>
 * <pre>{@code
 * @TrymigrateRegisterPlugin
 * private final TrymigrateLintersConfigurer linterConfig = config -> config
 *     .register(new MyCustomLinterProvider()) // Register a linter without SPI
 *     .configure("schemacrawler.tools.linter.LinterTableWithNoRemarks") // Configure a registered linter
 *         .severity(LintSeverity.high) // Set severity to high
 *         .tableInclusionPattern("APP_.*") // Apply only to tables starting with "APP_"
 *     .disable("schemacrawler.tools.linter.LinterTableSql"); // Disable a specific linter
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
         * Registers a new linter provider for the current test run. This avoids the need for
         * standard Java SPI discovery and allows direct registration of custom linters.
         *
         * @param linterProvider the provider to register
         * @return the configuration root for further chaining
         */
        TrymigrateLintersConfiguration register(LinterProvider linterProvider);

        /**
         * Enables and starts configuring a specific linter by its ID.
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
         * Discards any existing configuration for a linter and starts fresh. This is useful if you want to completely
         * redefine a linter's settings.
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
             * Sets custom configuration properties for the linter. These properties are passed directly to the linter.
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
             * Only tables matching this pattern will be checked by this linter.
             *
             * @param tableInclusionPattern the regex pattern for table inclusion
             * @return this configuration instance
             */
            TrymigrateLinterConfiguration tableInclusionPattern(String tableInclusionPattern);

            /**
             * Sets a regular expression to exclude specific tables from this linter.
             * Tables matching this pattern will be ignored by this linter, even if they match the inclusion pattern.
             *
             * @param tableExclusionPattern the regex pattern for table exclusion
             * @return this configuration instance
             */
            TrymigrateLinterConfiguration tableExclusionPattern(String tableExclusionPattern);

            /**
             * Sets a regular expression to restrict which columns this linter applies to.
             * Only columns matching this pattern will be checked by this linter.
             *
             * @param columnInclusionPattern the regex pattern for column inclusion
             * @return this configuration instance
             */
            TrymigrateLinterConfiguration columnInclusionPattern(String columnInclusionPattern);

            /**
             * Sets a regular expression to exclude specific columns from this linter.
             * Columns matching this pattern will be ignored by this linter, even if they match the inclusion pattern.
             *
             * @param columnExclusionPattern the regex pattern for column exclusion
             * @return this configuration instance
             */
            TrymigrateLinterConfiguration columnExclusionPattern(String columnExclusionPattern);
        }

    }

}
