package io.github.bekoenig.trymigrate.core.internal.lint.report;

import io.github.bekoenig.trymigrate.core.lint.report.LintsReporter;
import org.flywaydb.core.api.MigrationVersion;
import schemacrawler.schema.Catalog;
import schemacrawler.schemacrawler.IdentifiersBuilder;
import schemacrawler.tools.command.lint.options.LintOptionsBuilder;
import schemacrawler.tools.command.lint.options.LintReportOutputFormat;
import schemacrawler.tools.lint.Lints;
import schemacrawler.tools.lint.formatter.LintReportTextFormatter;
import schemacrawler.tools.lint.formatter.LintReportTextGenerator;
import schemacrawler.tools.options.OutputOptionsBuilder;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

public class LintsHtmlReporter implements LintsReporter {

    private final LintsReportPathResolver lintsReportResolver;

    public LintsHtmlReporter() {
        this.lintsReportResolver = new LintsReportPathResolver();
    }

    public void report(Catalog catalog, Lints lints, String schema, MigrationVersion migrationVersion) {
        Path outputFile = lintsReportResolver.resolve(schema, migrationVersion);

        LintReportTextFormatter lintReportTextFormatter = new LintReportTextFormatter(
                LintOptionsBuilder.builder().toOptions(),
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

}
