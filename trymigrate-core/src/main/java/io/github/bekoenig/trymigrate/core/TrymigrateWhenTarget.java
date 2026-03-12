package io.github.bekoenig.trymigrate.core;

import java.lang.annotation.*;

/**
 * Defines the target database migration version for a specific test method.
 * <p>
 * This annotation serves two primary purposes:
 * <ol>
 *     <li><b>Migration Control:</b> It tells Flyway to migrate the database to the exact version
 *     specified before the test is executed.</li>
 *     <li><b>Execution Ordering:</b> trymigrate automatically sorts tests based on their
 *     target version. Tests with lower versions are executed before tests with higher versions.</li>
 * </ol>
 * <p>
 * <b>Special Keywords:</b>
 * <ul>
 *     <li>{@code "latest"}: Migrates the database to the most recent version available in your migration scripts.</li>
 * </ul>
 * <p>
 * <b>Version Format:</b>
 * The value must follow Flyway's versioning rules (e.g., {@code "1.0"}, {@code "2.1.4"}).
 * It is parsed using {@link org.flywaydb.core.api.MigrationVersion#fromVersion(String)}.
 * <p>
 * <b>Interaction with other annotations:</b>
 * Combined with {@link TrymigrateGivenData}, you can seed data <i>before</i> this target version
 * is applied to verify data migration logic.
 *
 * @see Trymigrate
 * @see TrymigrateGivenData
 * @see TrymigrateCleanBefore
 */
@Target({ElementType.ANNOTATION_TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TrymigrateWhenTarget {

    /**
     * The target Flyway version to migrate to.
     * <p>
     * Use standard version strings like {@code "1.0"} or the keyword {@code "latest"}.
     *
     * @return target version string
     */
    String value();

}
