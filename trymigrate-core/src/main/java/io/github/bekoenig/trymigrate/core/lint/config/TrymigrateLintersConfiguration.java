package io.github.bekoenig.trymigrate.core.lint.config;

/**
 * Root interface to configure linters on a fluent way.
 *
 * @see TrymigrateLintersConfigurer
 */
public interface TrymigrateLintersConfiguration {

    /**
     * Enables a registered linter by adding a new config.
     *
     * @param linterId id of linter
     * @return intermediate interface for specific configuration of the enabled linter
     */
    TrymigrateLinterConfiguration enable(String linterId);

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
    TrymigrateLinterConfiguration reenable(String linterId);

}
