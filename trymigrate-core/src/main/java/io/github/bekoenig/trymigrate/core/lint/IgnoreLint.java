package io.github.bekoenig.trymigrate.core.lint;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(IgnoreLints.class)
public @interface IgnoreLint {

    String linterId();

    String objectName();

}
