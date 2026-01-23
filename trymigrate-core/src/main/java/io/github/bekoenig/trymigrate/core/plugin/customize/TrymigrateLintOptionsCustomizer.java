package io.github.bekoenig.trymigrate.core.plugin.customize;

import schemacrawler.tools.command.lint.options.LintOptions;
import schemacrawler.tools.text.options.BaseTextOptionsBuilder;

import java.util.function.Consumer;

/**
 * Customizer for {@link schemacrawler.tools.command.lint.options.LintOptions}.
 * <p>
 * Customizing is restricted to {@link BaseTextOptionsBuilder} because specific options of
 * {@link schemacrawler.tools.command.lint.options.LintOptionsBuilder} are different configured.
 * Use {@link TrymigrateLintersConfigurer} to change linter configuration
 * the fluent way.
 */
public interface TrymigrateLintOptionsCustomizer extends Consumer<BaseTextOptionsBuilder<?, LintOptions>> {
}
