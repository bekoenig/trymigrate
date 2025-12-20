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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Root annotation to activate database migration testing for test instance. Loads beans from test instance and all
 * plugins, prepares migration and executes all migrations. After each migration, the database model will be
 * checked for new lints. The lints are reported as HTML file and log message.
 * <p>
 * Use {@link TrymigrateTest} to add scenario data before migration, assert the database model after migration or
 * perform a clean before a migration.
 * <p>
 * Tests methods are executed in this order:
 * <ol>
 *     <li>Test methods annotated with {@link TrymigrateTest} ascending the target version</li>
 *     <li>Test methods annotated with {@link org.junit.jupiter.api.Test}</li>
 * </ol>
 * Sorting within a group is not unique. The order should therefore be explicitly defined via
 * {@link org.junit.jupiter.api.Order}. After executing the last annotated {@link TrymigrateTest}, all following
 * migrations are completed.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
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
