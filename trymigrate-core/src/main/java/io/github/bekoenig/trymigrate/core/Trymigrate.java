package io.github.bekoenig.trymigrate.core;

import io.github.bekoenig.trymigrate.core.config.TrymigratePlugin;
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

    String[] flywayProperties() default {};

    Class<? extends TrymigratePlugin>[] plugins() default TrymigratePlugin.class;

    LintSeverity failOn() default LintSeverity.low;

}
