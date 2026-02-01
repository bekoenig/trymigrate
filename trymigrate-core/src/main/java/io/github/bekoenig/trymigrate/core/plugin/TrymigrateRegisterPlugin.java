package io.github.bekoenig.trymigrate.core.plugin;

import java.lang.annotation.*;

/**
 * Annotation to mark a field (each visibility) in the test instance of {@link TrymigratePlugin#SUPPORTED_TYPES} as
 * plugin.
 * <p>
 * Provides optional support for autowrapping {@link org.testcontainers.containers.JdbcDatabaseContainer} as
 * {@link io.github.bekoenig.trymigrate.core.plugin.customize.TrymigrateDatabase}. The container will be started
 * before and stopped after test instance. Use a static field to avoid container stop after test instance for sharing
 * the container between multiple tests.
 */
@Target({ElementType.ANNOTATION_TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TrymigrateRegisterPlugin {
}
