package io.github.bekoenig.trymigrate.core.plugin;

import java.lang.annotation.*;

/**
 * Annotation to mark a field (each visibility) of the test instance as plugin to configure or customize
 * {@link io.github.bekoenig.trymigrate.core.Trymigrate}.
 */
@Target({ElementType.ANNOTATION_TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TrymigrateRegisterPlugin {
}
