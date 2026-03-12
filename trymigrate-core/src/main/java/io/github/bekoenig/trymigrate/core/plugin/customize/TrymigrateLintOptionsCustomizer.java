package io.github.bekoenig.trymigrate.core.plugin.customize;

import schemacrawler.tools.command.lint.options.LintOptions;
import schemacrawler.tools.text.options.BaseTextOptionsBuilder;

import java.util.function.Consumer;

/**
 * Customizer for general SchemaCrawler linting and text output options.
 * <p>
 * This interface provides access to the {@link BaseTextOptionsBuilder}, which allows you to
 * customize how lints are processed and formatted.
 * <p>
 * <b>Note:</b> While this customizer handles general output and processing options, use
 * {@link TrymigrateLintersConfigurer} for granular control over individual linters
 * (severity, patterns, etc.).
 * <p>
 * <b>Example:</b>
 * <pre>{@code
 * @TrymigrateRegisterPlugin
 * private final TrymigrateLintOptionsCustomizer options = builder -> builder
 *     .withTitle("My Custom Schema Report")
 *     .showUnlintedObjects(true);
 * }</pre>
 *
 * @see TrymigrateLintersConfigurer
 * @see schemacrawler.tools.command.lint.options.LintOptions
 */
public interface TrymigrateLintOptionsCustomizer extends Consumer<BaseTextOptionsBuilder<?, LintOptions>> {
}
