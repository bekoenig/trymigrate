package io.github.bekoenig.trymigrate.core.plugin;

import io.github.bekoenig.trymigrate.core.plugin.customize.*;
import org.flywaydb.core.api.callback.Callback;
import org.flywaydb.core.api.migration.JavaMigration;

import java.util.List;

/**
 * Marker interface for globally discoverable trymigrate plugins.
 * <p>
 * This interface is the root of trymigrate's SPI-based plugin system. Implementations of this interface
 * (or of one of its sub-interfaces) can be discovered globally and used to customize the migration,
 * inspection, and reporting lifecycle.
 * <p>
 * For test-local customization, you do <b>not</b> need to implement this interface. A field annotated with
 * {@link TrymigrateRegisterPlugin} may implement any of the supported extension interfaces listed in
 * {@link #SUPPORTED_TYPES}.
 * <p>
 * <b>Registration Mechanisms:</b>
 * <ol>
 *     <li><b>Manual Registration:</b> Use {@link TrymigrateRegisterPlugin} on a field within the test class.
 *     This is the most common approach and has the highest priority.</li>
 *     <li><b>Automatic Discovery:</b> Use {@link java.util.ServiceLoader} (SPI). Classes listed in
 *     {@code META-INF/services/io.github.bekoenig.trymigrate.core.plugin.TrymigratePlugin} are loaded automatically.</li>
 * </ol>
 * <p>
 * <b>Priority Model:</b>
 * <ul>
 *     <li>Plugins registered via {@link TrymigrateRegisterPlugin} always take precedence.</li>
 *     <li>For SPI-discovered plugins, priority is determined by the depth of the plugin interface hierarchy.
 *     Database-specific markers usually rank ahead of generic plugins.</li>
 * </ul>
 * <p>
 * <b>Supported Extension Points:</b>
 * <ul>
 *     <li>{@link TrymigrateFlywayCustomizer}: Configure Flyway locations, schemas, and properties.</li>
 *     <li>{@link TrymigrateDatabase}: Provide custom database containers or connections.</li>
 *     <li>{@link TrymigrateDataLoader}: Load custom seed data formats.</li>
 *     <li>{@link TrymigrateLintersConfigurer}: Fluently configure SchemaCrawler linters.</li>
 *     <li>{@link TrymigrateCatalogCustomizer}: Customize the database catalog crawl.</li>
 *     <li>{@link TrymigrateLintsReporter}: Add custom reporting (e.g., to external tools).</li>
 *     <li>{@link TrymigrateLintOptionsCustomizer}: Fine-tune general linting options.</li>
 *     <li>{@link Callback}: Register native Flyway callbacks.</li>
 *     <li>{@link JavaMigration}: Register native Flyway Java-based migrations.</li>
 * </ul>
 *
 * @see TrymigrateRegisterPlugin
 * @see TrymigrateDiscoverPlugins
 */
public interface TrymigratePlugin {

    /**
     * List of public extension interfaces that may be registered locally via
     * {@link TrymigrateRegisterPlugin}.
     */
    List<Class<?>> SUPPORTED_TYPES = List.of(
            TrymigrateCatalogCustomizer.class,
            TrymigrateDataLoader.class,
            TrymigrateFlywayCustomizer.class,
            TrymigrateLintersConfigurer.class,
            TrymigrateLintOptionsCustomizer.class,
            TrymigrateLintsReporter.class,
            TrymigrateDatabase.class,
            Callback.class,
            JavaMigration.class
    );

}
