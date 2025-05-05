package io.github.bekoenig.trymigrate.core.plugin;

public interface TrymigratePlugin {

    default void populate(TrymigrateBeanProvider beanProvider) {
    }

}
