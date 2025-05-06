package io.github.bekoenig.trymigrate.core;

import org.junit.jupiter.api.Test;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Enables database migration lifecycle management for a test method.
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Test
public @interface TrymigrateTest {

    /**
     * Target version of database model.
     *
     * @return Version for {@link org.flywaydb.core.api.MigrationVersion#fromVersion(String)}
     */
    String whenTarget();

    /**
     * Run clean before migration.
     *
     * @return {@code true} for clean; default is {@code false}
     */
    boolean cleanBefore() default false;

    /**
     * Scenario data applied before execution of migration to target.
     *
     * @return resources for {@link io.github.bekoenig.trymigrate.core.plugin.customize.TrymigrateDataLoader}
     */
    String[] givenData() default {};

}
