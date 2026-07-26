package io.github.bekoenig.trymigrate.core.plugin.customize;

import io.github.bekoenig.trymigrate.core.TrymigrateCatalogAttributes;
import schemacrawler.schema.Catalog;
import schemacrawler.tools.command.lint.options.LintOptions;
import schemacrawler.tools.lint.Lints;

/**
 * Plugin interface for custom lint reporting.
 * <p>
 * Implementations of this interface are called after each migration to report the detected
 * schema violations. This is the primary extension point for integrating trymigrate with
 * external reporting tools.
 * <p>
 * <b>Default Implementations:</b>
 * trymigrate provides built-in reporters for:
 * <ul>
 *     <li><b>Console:</b> Logs lints directly to the console for immediate feedback.</li>
 *     <li><b>HTML:</b> Generates detailed visual reports in {@code target/trymigrate-lint-reports/}.</li>
 * </ul>
 * <p>
 * <b>Custom Use Cases:</b>
 * You can implement this interface to:
 * <ul>
 *     <li>Post lint results to a Slack or Microsoft Teams channel.</li>
 *     <li>Export findings to a security auditing tool or a Jira ticket.</li>
 *     <li>Integrate with custom quality dashboards.</li>
 * </ul>
 * <p>
 * <b>Accessing Migration Context:</b>
 * The migration version and default schema are available as catalog attributes:
 * <pre>{@code
 * String migrationVersion = catalog.getAttribute(TrymigrateCatalogAttributes.MIGRATION_VERSION);
 * String defaultSchema = catalog.getAttribute(TrymigrateCatalogAttributes.DEFAULT_SCHEMA);
 * }</pre>
 * <p>
 * Register a reporter locally via
 * {@link io.github.bekoenig.trymigrate.core.plugin.TrymigrateRegisterPlugin}, or make it globally discoverable by
 * implementing {@link io.github.bekoenig.trymigrate.core.plugin.TrymigratePlugin} as well.
 *
 * @see TrymigrateCatalogAttributes
 */
public interface TrymigrateLintsReporter {

    /**
     * Reports detected lints for a specific migration version.
     * <p>
     * <b>Note:</b> The {@code lints} parameter contains only <b>new</b> violations introduced
     * by the current migration version (smart diffing). This is different from the {@code Lints}
     * test parameter injected into test methods, which represents the full current state.
     * <p>
     * Migration context (version and schema) is available via catalog attributes.
     *
     * @param catalog     the analyzed database model (includes migration context as attributes)
     * @param lints       the detected schema violations (delta since last version)
     * @param lintOptions the configuration options used for linting
     * @see TrymigrateCatalogAttributes
     */
    void report(Catalog catalog, Lints lints, LintOptions lintOptions);

}
