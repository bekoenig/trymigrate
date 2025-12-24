package io.github.bekoenig.trymigrate.core;

import java.lang.annotation.*;

/**
 * Limits target of database migration.
 */
@Target({ElementType.ANNOTATION_TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TrymigrateWhenTarget {

    /**
     * Target version of the database model.
     *
     * @return Version for {@link org.flywaydb.core.api.MigrationVersion#fromVersion(String)}
     */
    String value();

}
