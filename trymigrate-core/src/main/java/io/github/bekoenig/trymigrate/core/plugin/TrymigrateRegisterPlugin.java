package io.github.bekoenig.trymigrate.core.plugin;

import java.lang.annotation.*;

/**
 * Annotation used to register trymigrate plugins directly within a test class.
 * <p>
 * Apply this annotation to any field that implements one of the supported plugin interfaces
 * (see {@link TrymigratePlugin#SUPPORTED_TYPES}). Plugins registered this way have the
 * <b>highest priority</b> and override any SPI-discovered plugins.
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
