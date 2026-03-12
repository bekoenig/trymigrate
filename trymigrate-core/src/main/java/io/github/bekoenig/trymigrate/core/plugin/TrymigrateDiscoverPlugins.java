package io.github.bekoenig.trymigrate.core.plugin;

import java.lang.annotation.*;

/**
 * Configures the automatic discovery of plugins via Java's SPI ({@link java.util.ServiceLoader}).
 * <p>
 * By default, trymigrate automatically loads all implementations of {@link TrymigratePlugin} found
 * on the classpath. This annotation allows you to restrict or fine-tune this behavior for a specific
 * test class.
 * <p>
 * <b>Usage:</b>
 * This annotation is usually placed on the test class. If not present, all plugins are discovered
 * using the default settings.
 * <p>
 * <b>Restricting Discovery:</b>
 * Use the {@link #origin()} attribute to restrict discovery to a specific sub-interface hierarchy.
 * This is useful if you want to only load plugins related to a specific database or category.
 * <p>
 * <b>Excluding Plugins:</b>
 * Use the {@link #exclude()} attribute to explicitly block certain plugins (either by their class
 * or by an interface they implement) from being loaded.
 *
 * @see TrymigratePlugin
 * @see TrymigrateRegisterPlugin
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TrymigrateDiscoverPlugins {

    /**
     * The root plugin interface to use for discovery.
     * <p>
     * Only plugins implementing this specific interface will be discovered via {@link java.util.ServiceLoader}.
     * Defaults to {@link TrymigratePlugin} (all plugins).
     *
     * @return interface extension of {@link TrymigratePlugin}
     */
    Class<? extends TrymigratePlugin> origin() default TrymigratePlugin.class;

    /**
     * Explicitly excludes specific plugins or groups of plugins.
     * <p>
     * You can provide implementation classes to exclude single plugins, or interface classes
     * to exclude all implementations of that interface.
     *
     * @return list of interfaces or classes to exclude
     */
    Class<? extends TrymigratePlugin>[] exclude() default {};

}
