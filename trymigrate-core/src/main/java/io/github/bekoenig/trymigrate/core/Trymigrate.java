package io.github.bekoenig.trymigrate.core;

import io.github.bekoenig.trymigrate.core.plugin.TrymigrateDiscoverPlugins;
import io.github.bekoenig.trymigrate.core.internal.lifecycle.MigrateExecutor;
import io.github.bekoenig.trymigrate.core.internal.lifecycle.MigrateInitializer;
import io.github.bekoenig.trymigrate.core.internal.lifecycle.MigrateOrderer;
import io.github.bekoenig.trymigrate.core.internal.parameter.CatalogParameterResolver;
import io.github.bekoenig.trymigrate.core.internal.parameter.DataSourceParameterResolver;
import io.github.bekoenig.trymigrate.core.internal.parameter.LintsParameterResolver;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.*;

/**
 * Root annotation to activate database migration testing for a JUnit 5 test class.
 * <p>
 * This annotation orchestrates the entire trymigrate lifecycle:
 * <ul>
 *     <li>Starts the database and registers all plugins (customizers, data loaders, etc.).</li>
 *     <li>Automatically orders tests by their migration version (see {@link TrymigrateWhenTarget}).</li>
 *     <li>Executes Flyway migrations before each test.</li>
 *     <li>Injects database-related parameters into test methods.</li>
 *     <li>Performs schema linting and generates reports after <b>each migration</b>.</li>
 * </ul>
 * <p>
 * Adds support for method-level control via:
 * <ul>
 *     <li>{@link TrymigrateWhenTarget}: Defines the Flyway version to migrate to for a specific test (acting as a
 *     "verification point").</li>
 *     <li>{@link TrymigrateGivenData}: Seeds data into the database before the migration is applied.</li>
 *     <li>{@link TrymigrateCleanBefore}: Wipes the schema before executing the migration for a fresh state.</li>
 * </ul>
 * <p>
 * <b>Parameter Injection:</b> Test methods can receive the following parameters:
 * <ul>
 *     <li>{@link javax.sql.DataSource}: The live connection to the test database.</li>
 *     <li>{@link schemacrawler.schema.Catalog}: The database model of the Flyway managed schemas. Note that this
 *     includes the Flyway history table.</li>
 *     <li>{@link schemacrawler.tools.lint.Lints}: The detected schema violations (representing the <b>full current
 *     state</b> of Flyway-managed schemas, minus global excludes and the history table).</li>
 * </ul>
 * <p>
 * <b>Ordering:</b> Tests are executed in ascending order of their target versions. Tests without
 * {@link TrymigrateWhenTarget} are executed last. To define order within the same version, use
 * {@link org.junit.jupiter.api.Order}.
 * <p>
 * This annotation sets {@link TestInstance.Lifecycle#PER_CLASS} to ensure efficient resource management.
 *
 * @see TrymigrateWhenTarget
 * @see TrymigrateGivenData
 * @see TrymigrateCleanBefore
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@TestInstance(Lifecycle.PER_CLASS)
@ExtendWith({
        MigrateInitializer.class,
        MigrateExecutor.class,
        DataSourceParameterResolver.class,
        CatalogParameterResolver.class,
        LintsParameterResolver.class
})
@TestMethodOrder(MigrateOrderer.class)
@TrymigrateDiscoverPlugins
public @interface Trymigrate {
}
