package io.github.bekoenig.trymigrate.core.internal.schemacrawler.lint.report;

import io.github.bekoenig.trymigrate.core.lint.report.LintsReporter;
import io.github.bekoenig.trymigrate.core.lint.report.LintsMigrateInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import schemacrawler.schema.Catalog;
import schemacrawler.schemacrawler.IdentifiersBuilder;
import schemacrawler.tools.command.lint.options.LintOptionsBuilder;
import schemacrawler.tools.command.lint.options.LintReportOutputFormat;
import schemacrawler.tools.lint.Lints;
import schemacrawler.tools.lint.formatter.LintReportTextFormatter;
import schemacrawler.tools.lint.formatter.LintReportTextGenerator;
import schemacrawler.tools.options.OutputOptionsBuilder;

import java.io.StringWriter;

public class LintsLogReporter implements LintsReporter {

    private final Logger logger = LoggerFactory.getLogger(LintsLogReporter.class);

    public void report(Catalog catalog, Lints lints, LintsMigrateInfo migrateInfo) {
        logger.atInfo().setMessage(() -> createTextReport(catalog, lints)).log();
    }

    private String createTextReport(Catalog catalog, Lints lints) {
        StringWriter writer = new StringWriter();

        LintReportTextFormatter lintReportTextFormatter = new LintReportTextFormatter(
                LintOptionsBuilder.builder().toOptions(),
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
