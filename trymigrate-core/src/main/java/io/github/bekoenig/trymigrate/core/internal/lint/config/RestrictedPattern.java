package io.github.bekoenig.trymigrate.core.internal.lint.config;

/**
 * This is an extension for {@link LintersBuilder} to support additional include and exclude patterns in
 * {@link schemacrawler.tools.lint.config.LinterConfig} with restricted defaults.
 */
public record RestrictedPattern(String includePattern, String excludePattern) {

    public String overlayIncludePattern(String other) {
        if (other == null) {
            return includePattern();
        }

        // Combine two lookaheads for the entire line for a logical 'and'.
        // The following wildcard consumes all characters after match.
        return "(?=" + this.includePattern + "$)(?=" + other + "$).*";
    }

    public String overlayExcludePattern(String other) {
        if (other == null) {
            return excludePattern();
        }

        // Use logical 'or' to match any pattern.
        return "(" + this.excludePattern + ")|(" + other + ")";
    }

}
