package io.github.bekoenig.trymigrate.core.plugin.customize;

import schemacrawler.tools.lint.LintSeverity;
import schemacrawler.tools.lint.LinterProvider;

import java.util.Map;
import java.util.function.Consumer;

/**
 * Configurer for {@link TrymigrateLintersConfigurer.TrymigrateLintersConfiguration}. The entire entry point to add
 * fluent linter configurations.
 *
 * @see TrymigrateLintersConfigurer.TrymigrateLintersConfiguration
 */
public interface TrymigrateLintersConfigurer extends Consumer<TrymigrateLintersConfigurer.TrymigrateLintersConfiguration> {

    /**
     * Root interface to configure linters on a fluent way.
     *
     * @see TrymigrateLintersConfigurer
     */
    interface TrymigrateLintersConfiguration {

        /**
         * Registers a linter provider avoiding SPI.
         *
         * @param linterProvider provider for linter
         * @return root interface
         */
        TrymigrateLintersConfiguration register(LinterProvider linterProvider);

        /**
         * Enables a registered linter by adding a new config.
         *
         * @param linterId id of linter
         * @return intermediate interface for specific configuration of the enabled linter
         */
        TrymigrateLinterConfiguration configure(String linterId);

        /**
         * Disables an enabled linter by removing all configs.
         *
         * @param linterId id of linter
         * @return root interface
         */
        TrymigrateLintersConfiguration disable(String linterId);

        /**
         * Re-enables a linter by removing all configs and adding a new config.
         *
         * @param linterId id of linter
         * @return intermediate interface for specific configuration of the enabled linter
         */
        TrymigrateLinterConfiguration reconfigure(String linterId);

        /**
         * Intermediate interface to add specific configurations to the enabled linter or configure the next linter.
         *
         * @see TrymigrateLintersConfigurer
         * @see TrymigrateLintersConfiguration
         */
        interface TrymigrateLinterConfiguration extends TrymigrateLintersConfiguration {

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

    }


}
