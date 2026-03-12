package io.github.bekoenig.trymigrate.core.plugin.customize;

import org.flywaydb.core.api.MigrationVersion;
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
 */
public interface TrymigrateLintsReporter {

    /**
     * Reports detected lints for a specific migration version.
     * <p>
     * <b>Note:</b> The {@code lints} parameter contains only <b>new</b> violations introduced
     * by the current migration version (Smart Diffing).
     *
     * @param catalog          the analyzed database model
     * @param lints            the detected schema violations (delta since last version)
     * @param schema           the name of the schema being analyzed
     * @param migrationVersion the current Flyway migration version
     * @param lintOptions      the configuration options used for linting
     */
    void report(Catalog catalog, Lints lints, String schema, MigrationVersion migrationVersion, LintOptions lintOptions);

}
