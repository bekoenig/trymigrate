package io.github.bekoenig.trymigrate.core.internal.lint.report.html;

import io.github.bekoenig.trymigrate.core.lint.report.LintsReporter;
import org.flywaydb.core.api.MigrationVersion;
import schemacrawler.schema.Catalog;
import schemacrawler.schemacrawler.IdentifiersBuilder;
import schemacrawler.tools.command.lint.options.LintOptions;
import schemacrawler.tools.command.lint.options.LintReportOutputFormat;
import schemacrawler.tools.lint.Lints;
import schemacrawler.tools.lint.formatter.LintReportTextFormatter;
import schemacrawler.tools.lint.formatter.LintReportTextGenerator;
import schemacrawler.tools.options.OutputOptionsBuilder;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

public class LintsHtmlReporter implements LintsReporter {

    public static final String PROPERTY_NAME_SKIP_EMPTY = "trymigrate.lint.reports.html.skip-empty";

    private final LintOptions lintOptions;
    private final LintsHtmlReporterFileResolver lintsReportResolver;

    public LintsHtmlReporter(LintOptions lintOptions) {
        this.lintOptions = lintOptions;
        this.lintsReportResolver = new LintsHtmlReporterFileResolver();
    }

    public void report(Catalog catalog, Lints lints, String schema, MigrationVersion migrationVersion) {
        if (lints.isEmpty() && skipEmpty()) {
            return;
        }

        Path outputFile = lintsReportResolver.resolve(schema, migrationVersion);

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

}
