package io.github.bekoenig.trymigrate.core.plugin;

import java.lang.annotation.*;
/**
 * Annotation used to register trymigrate extensions directly within a test class.
 * <p>
 * Apply this annotation to any field that implements one of the supported public extension interfaces
 * (see {@link TrymigratePlugin#SUPPORTED_TYPES}), or to supported Flyway types such as
 * {@code Callback} and {@code JavaMigration}. Registrations made this way have the
 * <b>highest priority</b> and override globally discovered SPI plugins.
 * <p>
 * <b>Local Configuration:</b>
 * This is the primary way to provide test-specific configurations that shouldn't affect the
 * entire project. It allows for modular, granular control within a single test class.
 * It is also the simplest option because the registered field does not need to implement
 * {@link TrymigratePlugin}.
 * <p>
 * <b>Native Testcontainers Support:</b>
 * This annotation has special support for {@code org.testcontainers.containers.JdbcDatabaseContainer}.
 * trymigrate automatically manages the container's lifecycle (start/stop) and integrates it as a
 * {@link io.github.bekoenig.trymigrate.core.plugin.customize.TrymigrateDatabase} plugin.
 * <p>
 * <b>Lifecycle &#38; Container Reuse:</b>
 * <ul>
 *     <li><b>Instance Fields:</b> The container is started before the test class and stopped
 *     immediately after all tests in the class are finished.</li>
 *     <li><b>Static Fields:</b> The container is started once and shared across multiple test classes.
 *     It is only stopped when the JVM exits. This is highly recommended for large test suites to save startup time.</li>
 * </ul>
 *
 * @see TrymigratePlugin
 * @see TrymigrateDiscoverPlugins
 */
@Target({ElementType.ANNOTATION_TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TrymigrateRegisterPlugin {
}
