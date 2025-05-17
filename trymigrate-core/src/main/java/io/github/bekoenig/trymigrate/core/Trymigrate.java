package io.github.bekoenig.trymigrate.core;

import io.github.bekoenig.trymigrate.core.lint.ExcludeLint;
import io.github.bekoenig.trymigrate.core.lint.SuppressLint;
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
 * Root annotation to activate database migration testing for test instance.
 * <p>
 * Enables support for {@link io.github.bekoenig.trymigrate.core.plugin.TrymigrateBean} on test instance.
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
     * Allows explicit plugin selection for multiple plugin branches.
     *
     * @return interface extension of {@link TrymigratePlugin}
     */
    Class<? extends TrymigratePlugin> plugin() default TrymigratePlugin.class;

    /**
     * Threshold to fail on lints. Indicates mistakes in database model.
     *
     * @see ExcludeLint
     * @see SuppressLint
     * @return lower boundary
     */
    LintSeverity failOn() default LintSeverity.low;

}
