package io.github.bekoenig.trymigrate.core.plugin;

import io.github.bekoenig.trymigrate.core.plugin.customize.*;
import org.flywaydb.core.api.callback.Callback;
import org.flywaydb.core.api.migration.JavaMigration;

import java.util.List;

/**
 * Marker interface for all trymigrate plugins.
 * <p>
 * This interface is the foundation of the extension's plugin system. Implementations of this interface
 * (or its sub-interfaces) can be registered to customize the migration, inspection, and reporting lifecycle.
 * <p>
 * <b>Registration Mechanisms:</b>
 * <ol>
 *     <li><b>Manual Registration:</b> Using {@link TrymigrateRegisterPlugin} on a field within the test class.
 *     This is the most common way and has the highest priority.</li>
 *     <li><b>Automatic Discovery:</b> Using {@link java.util.ServiceLoader} (SPI). Plugins defined in
 *     {@code META-INF/services/io.github.bekoenig.trymigrate.core.plugin.TrymigratePlugin} are automatically loaded.</li>
 * </ol>
 * <p>
 * <b>Priority Model:</b>
 * <ul>
 *     <li>Plugins registered via {@link TrymigrateRegisterPlugin} always take precedence.</li>
 *     <li>For SPI-discovered plugins, the priority is determined by the depth of the interface hierarchy.
 *     Database-specific plugins usually have higher priority than core plugins.</li>
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
     * List of all supported plugin interfaces that can be registered.
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
