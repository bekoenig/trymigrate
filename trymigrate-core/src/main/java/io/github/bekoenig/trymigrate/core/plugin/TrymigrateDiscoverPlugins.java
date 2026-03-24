package io.github.bekoenig.trymigrate.core.plugin;

import java.lang.annotation.*;

/**
 * Configures the automatic discovery of plugins via Java's SPI ({@link java.util.ServiceLoader}).
 * <p>
 * By default, trymigrate automatically loads all SPI plugins rooted at {@link TrymigratePlugin} that are
 * available on the classpath. This annotation allows you to restrict or fine-tune that discovery
 * for a specific test class.
 * <p>
 * <b>Usage:</b>
 * Place this annotation on the test class. If not present, all SPI plugins are discovered using the
 * default settings.
 * <p>
 * <b>Restricting Discovery:</b>
 * Use the {@link #origin()} attribute to restrict discovery to a specific plugin hierarchy.
 * This is especially useful with database-specific marker interfaces such as
 * {@code TrymigratePostgreSQLPlugin} or {@code TrymigrateH2Plugin}.
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
     * Only SPI plugins that belong to this specific interface hierarchy will be discovered.
     * Defaults to {@link TrymigratePlugin}, which means all trymigrate SPI plugins.
     *
     * @return interface extension of {@link TrymigratePlugin}
     */
    Class<? extends TrymigratePlugin> origin() default TrymigratePlugin.class;

    /**
     * Explicitly excludes specific plugins or groups of plugins.
     * <p>
     * You can provide implementation classes to exclude single plugins, or interface classes
     * to exclude all implementations in that branch of the hierarchy.
     *
     * @return list of interfaces or classes to exclude
     */
    Class<? extends TrymigratePlugin>[] exclude() default {};

}
