package io.github.bekoenig.trymigrate.core.lint;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(AcceptLints.class)
public @interface AcceptLint {

    String linterId() default ".*";

    String objectName() default ".*";

}
