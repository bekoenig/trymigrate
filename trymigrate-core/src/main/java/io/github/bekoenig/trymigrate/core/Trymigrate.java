package io.github.bekoenig.trymigrate.core;

import io.github.bekoenig.trymigrate.core.lint.TrymigrateExcludeLint;
import io.github.bekoenig.trymigrate.core.lint.TrymigrateSuppressLint;
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
import org.junit.platform.commons.annotation.Testable;
import schemacrawler.tools.lint.LintSeverity;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Root annotation to activate database migration testing for test instance. Loads beans from test instance and all
 * plugins, prepares migration and executes all migration steps. After each migration, the database model will be
 * checked for new lints.
 * <p>
 * Use {@link TrymigrateTest} to add scenario data before migration, assert the database model after migration or
 * perform a clean before a migration.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@TestInstance(Lifecycle.PER_CLASS)
@Testable
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
     * Properties for flyway. Format is {@code [key]=[value]}. Prefix {@code flyway.*} is optional for keys.
     *
     * @return array of properties for flyway
     */
    String[] flywayProperties() default {};

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

    /**
     * Threshold to fail on lints. Indicates mistakes in the database model on migration.
     *
     * @see TrymigrateExcludeLint
     * @see TrymigrateSuppressLint
     * @return lower boundary
     */
    LintSeverity failOn() default LintSeverity.low;

}
