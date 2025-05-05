package io.github.bekoenig.trymigrate.core.plugin;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to mark a field (each visibility) as injectable bean to configure or customize
 * {@link io.github.bekoenig.trymigrate.core.Trymigrate}.
 * <p>
 * Supported for fields in
 * <ul>
 *     <li>test instances (highest priority)</li>
 *     <li>plugins (medium priority; see {@link TrymigratePlugin})</li>
 *     <li>built-in defaults (lowest priority)</li>
 * </ul>
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface TrymigrateBean {

    /**
     * Default value for nullable attribute of this annotation.
     */
    boolean NULLABLE_DEFAULT = false;

    /**
     * Indicates that this bean is optional and can be null.
     * When {@code false} and the field has a null value on wiring an exception will raise to fail fast.
     *
     * @return {@code true} on nullable (default is {@link TrymigrateBean#NULLABLE_DEFAULT})
     */
    boolean nullable() default NULLABLE_DEFAULT;

}
