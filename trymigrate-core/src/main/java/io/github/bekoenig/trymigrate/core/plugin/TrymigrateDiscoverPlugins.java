package io.github.bekoenig.trymigrate.core.plugin;

import java.lang.annotation.*;

/**
 * Configures plugin discovery.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TrymigrateDiscoverPlugins {

    /**
     * Defines the plugin interface for discovery. Loads all ancestors and descendant plugins.
     *
     * @return interface extension of {@link TrymigratePlugin}
     */
    Class<? extends TrymigratePlugin> origin() default TrymigratePlugin.class;

    /**
     * Exclude single plugins by class or a set of plugins using interface.
     *
     * @return interface or class of type {@link TrymigratePlugin}
     */
    Class<? extends TrymigratePlugin>[] exclude() default {};

}
