package io.github.bekoenig.trymigrate.core;

import io.github.bekoenig.trymigrate.core.plugin.TrymigratePlugin;
import io.github.bekoenig.trymigrate.core.internal.jupiter.extension.MigrateCompleter;
import io.github.bekoenig.trymigrate.core.internal.jupiter.extension.MigrateExecutor;
import io.github.bekoenig.trymigrate.core.internal.jupiter.extension.MigrateInitializer;
import io.github.bekoenig.trymigrate.core.internal.jupiter.order.TargetOrder;
import io.github.bekoenig.trymigrate.core.internal.jupiter.resolver.CatalogParameterResolver;
import io.github.bekoenig.trymigrate.core.internal.jupiter.resolver.DataSourceParameterResolver;
import io.github.bekoenig.trymigrate.core.internal.jupiter.resolver.LintsParameterResolver;
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
@TestMethodOrder(TargetOrder.class)
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
     * @see io.github.bekoenig.trymigrate.core.lint.IgnoreLint
     * @see io.github.bekoenig.trymigrate.core.lint.AcceptLint
     * @return lower boundary
     */
    LintSeverity failOn() default LintSeverity.low;

}
