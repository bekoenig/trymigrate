package io.github.bekoenig.trymigrate.core.internal.schemacrawler.lint.report;

import io.github.bekoenig.trymigrate.core.lint.LintsReportResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;

public class DefaultLintsReportResolver implements LintsReportResolver {

    private static final Path DEFAULT_OUTPUT_DIRECTORY = Path.of("./");

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private Path resolveOutputDirectory() {
        try {
            return Path.of(Objects.requireNonNull(ClassLoader.getSystemResource("")).toURI()).getParent();
        } catch (Exception e) {
            logger.error("Failed to resolve target folder. Use {} as fallback.", DEFAULT_OUTPUT_DIRECTORY, e);
            return DEFAULT_OUTPUT_DIRECTORY;
        }
    }

    @Override
    public Optional<Path> resolve(MigrateInfo migrateInfo) {
        Path reportFolder = resolveOutputDirectory()
                .resolve("trymigrate-lint-reports")
                .resolve(Objects.requireNonNullElse(migrateInfo.schema(), "schema-undefined"));

        try {
            Files.createDirectories(reportFolder);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to create folder", e);
        }

        return Optional.of(reportFolder
                .resolve(migrateInfo.descriptor().replaceAll("\\.", "_") + ".html"));
    }

}
