package io.github.bekoenig.trymigrate.core.internal.lint.report;

import io.github.bekoenig.trymigrate.core.TrymigrateCatalogAttributes;
import io.github.bekoenig.trymigrate.core.internal.OutputPathResolver;
import io.github.bekoenig.trymigrate.core.plugin.TrymigratePlugin;
import io.github.bekoenig.trymigrate.core.plugin.customize.TrymigrateLintsReporter;
import schemacrawler.schema.Catalog;
import schemacrawler.schema.IdentifiersBuilder;
import schemacrawler.tools.command.lint.options.LintOptions;
import schemacrawler.tools.command.lint.options.LintReportOutputFormat;
import schemacrawler.tools.lint.Lints;
import schemacrawler.tools.lint.formatter.LintReportTextFormatter;
import schemacrawler.tools.lint.formatter.LintReportTextGenerator;
import schemacrawler.tools.options.OutputOptionsBuilder;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

public class LintsHtmlReporter implements TrymigrateLintsReporter, TrymigratePlugin {

    public static final String PROPERTY_NAME_SKIP_EMPTY = "trymigrate.lint.report.html.skip-empty";
    public static final String PROPERTY_NAME = "trymigrate.lint.report.html.basedir";

    private final OutputPathResolver pathResolver = new OutputPathResolver(PROPERTY_NAME, "trymigrate-lint-reports");

    @Override
    public void report(Catalog catalog, Lints lints, LintOptions lintOptions) {
        if (lints.isEmpty() && skipEmpty()) {
            return;
        }

        String defaultSchema = catalog.getAttribute(TrymigrateCatalogAttributes.DEFAULT_SCHEMA);
        String migrationVersion = catalog.getAttribute(TrymigrateCatalogAttributes.MIGRATION_VERSION);

        Path outputFile = resolve(defaultSchema, migrationVersion);

        LintReportTextFormatter lintReportTextFormatter = new LintReportTextFormatter(
                lintOptions,
                OutputOptionsBuilder.builder()
                        .withOutputFormat(LintReportOutputFormat.html)
                        .withOutputEncoding(StandardCharsets.UTF_8)
                        .withOutputFile(outputFile)
                        .toOptions(),
                IdentifiersBuilder.builder().toOptions());

        LintReportTextGenerator lintReportTextGenerator = new LintReportTextGenerator();
        lintReportTextGenerator.setCatalog(catalog);
        lintReportTextGenerator.setHandler(lintReportTextFormatter);
        lintReportTextGenerator.generateLintReport(lints);
    }

    private boolean skipEmpty() {
        return Boolean.parseBoolean(System.getProperty(PROPERTY_NAME_SKIP_EMPTY, Boolean.TRUE.toString()));
    }

    protected Path resolve(String schema, String migrationVersion) {
        return pathResolver.resolve(schema, getReportFileName(migrationVersion));
    }

    private String getReportFileName(String migrationVersion) {
        return migrationVersion.replaceAll("\\.", "_") + ".html";
    }

}
