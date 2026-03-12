package io.github.bekoenig.trymigrate.core;

import java.lang.annotation.*;

/**
 * Marks a test method to trigger a database wipe before its migration is executed.
 * <p>
 * This annotation ensures that the database is in a completely empty state (using Flyway's {@code clean} command)
 * before the migration to the version specified by {@link TrymigrateWhenTarget} begins.
 * <p>
 * <b>Usage Scenario:</b>
 * This is particularly important when:
 * <ul>
 *     <li>Sharing a database container across multiple tests (see
 *     {@link io.github.bekoenig.trymigrate.core.plugin.TrymigrateRegisterPlugin}).</li>
 *     <li>Testing non-incremental migration scenarios where a clean baseline is required.</li>
 *     <li>Ensuring that data seeded in previous tests doesn't interfere with the current test.</li>
 * </ul>
 * <p>
 * <b>Requirement:</b>
 * Flyway 9.x and above disable the {@code clean} command by default. To use this annotation,
 * you <b>must</b> explicitly enable it in your
 * {@link io.github.bekoenig.trymigrate.core.plugin.customize.TrymigrateFlywayCustomizer}:
 * <pre>{@code
 * @TrymigrateRegisterPlugin
 * private final TrymigrateFlywayCustomizer flyway = config -> config.cleanDisabled(false);
 * }</pre>
 *
 * @see TrymigrateWhenTarget
 * @see io.github.bekoenig.trymigrate.core.plugin.customize.TrymigrateFlywayCustomizer
 */
@Target({ElementType.ANNOTATION_TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TrymigrateCleanBefore {
}
