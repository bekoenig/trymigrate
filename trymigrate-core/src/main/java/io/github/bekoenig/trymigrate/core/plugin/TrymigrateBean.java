package io.github.bekoenig.trymigrate.core.plugin;

import java.lang.annotation.*;

/**
 * Annotation to mark a field (each visibility) as injectable bean to configure or customize
 * {@link io.github.bekoenig.trymigrate.core.Trymigrate}.
 * <p>
 * Supported for fields in test instances or {@link TrymigratePlugin}.
 * <p>
 * Use {@link org.junit.jupiter.api.Order} to explicit the priority in test instance or the same plugin hierarchy level.
 */
@Target({ElementType.ANNOTATION_TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TrymigrateBean {
}
