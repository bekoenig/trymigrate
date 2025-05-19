package io.github.bekoenig.trymigrate.core.internal.lint.report;

import io.github.bekoenig.trymigrate.core.lint.report.LintsReporter;
import org.flywaydb.core.api.MigrationVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import schemacrawler.schema.Catalog;
import schemacrawler.schemacrawler.IdentifiersBuilder;
import schemacrawler.tools.command.lint.options.LintOptions;
import schemacrawler.tools.command.lint.options.LintReportOutputFormat;
import schemacrawler.tools.lint.Lints;
import schemacrawler.tools.lint.formatter.LintReportTextFormatter;
import schemacrawler.tools.lint.formatter.LintReportTextGenerator;
import schemacrawler.tools.options.OutputOptionsBuilder;

import java.io.StringWriter;

public class LintsLogReporter implements LintsReporter {

    private final Logger logger = LoggerFactory.getLogger(LintsLogReporter.class);

    private final LintOptions lintOptions;

    public LintsLogReporter(LintOptions lintOptions) {
        this.lintOptions = lintOptions;
    }

    public void report(Catalog catalog, Lints lints, String schema, MigrationVersion migrationVersion) {
        logger.atInfo().setMessage(() -> createTextReport(catalog, lints)).log();
    }

    private String createTextReport(Catalog catalog, Lints lints) {
        StringWriter writer = new StringWriter();

        LintReportTextFormatter lintReportTextFormatter = new LintReportTextFormatter(
                lintOptions,
                OutputOptionsBuilder.builder()
                        .withOutputFormat(LintReportOutputFormat.text)
                        .withOutputWriter(writer)
                        .toOptions(),
                IdentifiersBuilder.builder().toOptions());

        LintReportTextGenerator lintReportTextGenerator = new LintReportTextGenerator();
        lintReportTextGenerator.setCatalog(catalog);
        lintReportTextGenerator.setHandler(lintReportTextFormatter);
        lintReportTextGenerator.generateLintReport(lints);

        return writer.toString();
    }

}
