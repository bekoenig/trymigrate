package io.github.bekoenig.trymigrate.core.internal.catalog.export;

import io.github.bekoenig.trymigrate.core.TrymigrateCatalogAttributes;
import io.github.bekoenig.trymigrate.core.internal.OutputPathResolver;
import io.github.bekoenig.trymigrate.core.plugin.TrymigratePlugin;
import io.github.bekoenig.trymigrate.core.plugin.customize.TrymigrateCatalogExporter;
import schemacrawler.schema.Catalog;
import schemacrawler.tools.executable.SchemaCrawlerExecutable;
import schemacrawler.tools.options.Config;
import schemacrawler.tools.options.ConfigUtility;
import schemacrawler.tools.options.OutputOptionsBuilder;

import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Exports the catalog as OKF (Open Knowledge Framework) documentation using SchemaCrawler Scribe.
 * <p>
 * Generates expandable Markdown documentation with ER diagrams suitable for
 * Obsidian, MkDocs, or similar documentation systems.
 * <p>
 * Output is written to {@code target/trymigrate-scribe/{schema}/} by default.
 * The base directory can be customized via the system property {@link ScribeOkfExporter#PROPERTY_BASEDIR}.
 *
 * @since 1.4.2
 */
public class ScribeOkfExporter implements TrymigrateCatalogExporter, TrymigratePlugin {

    private static final Logger LOGGER = Logger.getLogger(ScribeOkfExporter.class.getName());

    public static final String PROPERTY_BASEDIR = "trymigrate.scribe.basedir";

    private final OutputPathResolver pathResolver = new OutputPathResolver(PROPERTY_BASEDIR, "trymigrate-scribe");

    @Override
    public void export(Catalog catalog) {
        String defaultSchema = catalog.getAttribute(TrymigrateCatalogAttributes.DEFAULT_SCHEMA);
        Path outputPath = pathResolver.resolve(defaultSchema);

        Config config = ConfigUtility.newConfig();
        config.put("schemacrawler.scribe.expanded-output", true);

        SchemaCrawlerExecutable executable = new SchemaCrawlerExecutable("scribe");
        executable.setCatalog(catalog);
        executable.setAdditionalConfiguration(config);
        executable.setOutputOptions(OutputOptionsBuilder.builder()
                .withOutputFile(outputPath)
                .withOutputFormatValue("okf")
                .toOptions());

        try {
            executable.execute();
            LOGGER.log(Level.INFO, "Exported catalog to {0}", outputPath);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to export catalog to " + outputPath, e);
        }
    }

}
