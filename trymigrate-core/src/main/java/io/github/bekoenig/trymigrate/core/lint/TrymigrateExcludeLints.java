package io.github.bekoenig.trymigrate.core.lint;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Meta annotation to add support for repeatable usage of {@link TrymigrateExcludeLint}.
 * <p>
 * Only necessary for compile time.
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface TrymigrateExcludeLints {

    TrymigrateExcludeLint[] value();

}
