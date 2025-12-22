package io.github.bekoenig.trymigrate.core;

import io.github.bekoenig.trymigrate.core.plugin.TrymigratePlugin;
import io.github.bekoenig.trymigrate.core.internal.lifecycle.MigrateCompleter;
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
 * Root annotation to activate database migration testing for test instance. Loads beans from test instance and all
 * plugins, prepares migration and executes all migrations. After each migration, the database model will be
 * checked for new lints. The lints are reported as HTML file and log message.
 * <p>
 * Adds support for
 * <ol>
 *     <li>{@link TrymigrateCleanBefore} to run a clean before migrate</li>
 *     <li>{@link TrymigrateGivenData} to add scenario data before target migration is applied</li>
 *     <li>{@link TrymigrateWhenTarget} to set the target version of migration</li>
 * </ol>
 * on test methods (annotated with {@link org.junit.jupiter.api.Test}).
 * <p>
 * Tests annotated with {@link TrymigrateWhenTarget} are executed at first, in ascending order of their target version.
 * All other tests are executed afterward. Sorting within a group is not unique. The order should therefore be
 * explicitly defined via {@link org.junit.jupiter.api.Order}.
 * <p>
 * Enables parameter providers for
 * <ul>
 *     <li>{@link javax.sql.DataSource}: Datasource to database</li>
 *     <li>{@link schemacrawler.schema.Catalog}: Database model after last migration (system schemas may be excluded)</li>
 *     <li>{@link schemacrawler.tools.lint.Lints}: Lints of migrated schemas</li>
 * </ul>
 * on test methods.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@TestInstance(Lifecycle.PER_CLASS)
@ExtendWith({
        MigrateInitializer.class,
        MigrateExecutor.class,
        MigrateCompleter.class,
        DataSourceParameterResolver.class,
        CatalogParameterResolver.class,
        LintsParameterResolver.class
})
@TestMethodOrder(MigrateOrderer.class)
public @interface Trymigrate {

    /**
     * Restricts the plugin discovery to a subtree of plugins.
     *
     * @return interface extension of {@link TrymigratePlugin}
     */
    Class<? extends TrymigratePlugin> discoverPlugin() default TrymigratePlugin.class;

    /**
     * Excludes plugin subtrees using interfaces or single plugins using class.
     *
     * @return interface or class of type {@link TrymigratePlugin}
     */
    Class<? extends TrymigratePlugin>[] excludePlugins() default {};

}
