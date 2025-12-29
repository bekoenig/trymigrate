package io.github.bekoenig.trymigrate.core.internal.lint.report;

import io.github.bekoenig.trymigrate.core.lint.report.TrymigrateLintsReporter;
import org.flywaydb.core.api.MigrationVersion;
import schemacrawler.schema.Catalog;
import schemacrawler.schemacrawler.IdentifiersBuilder;
import schemacrawler.tools.command.lint.options.LintOptions;
import schemacrawler.tools.command.lint.options.LintReportOutputFormat;
import schemacrawler.tools.lint.Lints;
import schemacrawler.tools.lint.formatter.LintReportTextFormatter;
import schemacrawler.tools.lint.formatter.LintReportTextGenerator;
import schemacrawler.tools.options.OutputOptionsBuilder;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class LintsHtmlReporter implements TrymigrateLintsReporter {

    public static final String PROPERTY_NAME_SKIP_EMPTY = "trymigrate.lint.reports.html.skip-empty";
    public static final String PROPERTY_NAME = "trymigrate.lint.reports.html.basedir";

    public void report(Catalog catalog, Lints lints, String schema, MigrationVersion migrationVersion,
                       LintOptions lintOptions) {
        if (lints.isEmpty() && skipEmpty()) {
            return;
        }

        Path outputFile = resolve(schema, migrationVersion);

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

    protected Path resolve(String schema, MigrationVersion migrationVersion) {
        Path reportFolder = getBaseDir()
                .resolve("trymigrate-lint-reports")
                .resolve(Objects.requireNonNullElse(schema, "schema-undefined"));

        try {
            Files.createDirectories(reportFolder);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to create folder", e);
        }

        return reportFolder.resolve(getReportFileName(migrationVersion));
    }

    private Path getBaseDir() {
        String property = System.getProperty(PROPERTY_NAME);
        if (property != null) {
            return Path.of(property);
        }
        return getTargetFolder();
    }

    private Path getTargetFolder() {
        try {
            // target folder is parent of system resource folder
            return Path.of(Objects.requireNonNull(ClassLoader.getSystemResource("./")).toURI()).getParent();
        } catch (URISyntaxException e) {
            throw new IllegalStateException("Failed to get root uri for system resources of class loader", e);
        }
    }

    private String getReportFileName(MigrationVersion migrationVersion) {
        return migrationVersion.getVersion().replaceAll("\\.", "_") + ".html";
    }

}
