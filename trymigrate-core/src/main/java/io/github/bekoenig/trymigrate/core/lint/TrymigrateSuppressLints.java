package io.github.bekoenig.trymigrate.core.lint;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Meta annotation to add support for repeatable usage of {@link TrymigrateSuppressLint}.
 * <p>
 * Only necessary for compile time.
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface TrymigrateSuppressLints {

    TrymigrateSuppressLint[] value();

}
