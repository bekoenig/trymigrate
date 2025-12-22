package io.github.bekoenig.trymigrate.core;

import org.junit.jupiter.api.Test;

import java.lang.annotation.*;

/**
 * Enables database migration lifecycle management for a test method in a {@link Trymigrate} test class.
 * <p>
 * Annotated methods supports
 * <ul>
 *     <li>{@link javax.sql.DataSource}: Datasource to database</li>
 *     <li>{@link schemacrawler.schema.Catalog}: Database model after last migration (system schemas may be excluded)</li>
 *     <li>{@link schemacrawler.tools.lint.Lints}: Lints of migrated schemas</li>
 * </ul>
 * as parameters for further asserts.
 *
 * @see Trymigrate
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Test
public @interface TrymigrateTest {

    /**
     * Target version of the database model.
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
     * Scenario data applied before execution of migration to target version.
     *
     * @return resources for {@link io.github.bekoenig.trymigrate.core.plugin.customize.TrymigrateDataLoader}
     */
    String[] givenData() default {};

}
