package io.github.bekoenig.trymigrate.core.internal.schemacrawler.lint.report;

import io.github.bekoenig.trymigrate.core.lint.report.LintsReporter;
import io.github.bekoenig.trymigrate.core.lint.report.LintsReportResolver;
import io.github.bekoenig.trymigrate.core.lint.report.LintsMigrateInfo;
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
import java.util.Optional;

public class LintsHtmlReporter implements LintsReporter {

    private final LintsReportResolver lintsReportResolver;

    public LintsHtmlReporter(LintsReportResolver lintsReportResolver) {
        this.lintsReportResolver = lintsReportResolver;
    }

    public void report(Catalog catalog, Lints lints, LintsMigrateInfo migrateInfo) {
        Optional<Path> outputFile = lintsReportResolver.resolve(migrateInfo);
        if (outputFile.isEmpty()) {
            return;
        }

        LintReportTextFormatter lintReportTextFormatter = new LintReportTextFormatter(
                LintOptionsBuilder.builder().toOptions(),
                OutputOptionsBuilder.builder()
                        .withOutputFormat(LintReportOutputFormat.html)
                        .withOutputEncoding(StandardCharsets.UTF_8)
                        .withOutputFile(outputFile.get())
                        .toOptions(),
                IdentifiersBuilder.builder().toOptions());

        LintReportTextGenerator lintReportTextGenerator = new LintReportTextGenerator();
        lintReportTextGenerator.setCatalog(catalog);
        lintReportTextGenerator.setHandler(lintReportTextFormatter);
        lintReportTextGenerator.generateLintReport(lints);
    }

}
