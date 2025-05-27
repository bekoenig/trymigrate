package io.github.bekoenig.trymigrate.core.lint.config;

/**
 * Root interface to configure linters on a fluent way.
 *
 * @see TrymigrateLintersCustomizer
 */
public interface TrymigrateLintersConfiguration {

    /**
     * Enables a registered linter.
     *
     * @param linterId id of linter
     * @return intermediate interface for specific configuration of the enabled linter
     */
    TrymigrateLinterConfiguration enable(String linterId);

}
