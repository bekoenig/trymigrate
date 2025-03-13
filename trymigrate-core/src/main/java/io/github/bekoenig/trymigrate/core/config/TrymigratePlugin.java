package io.github.bekoenig.trymigrate.core.config;

public interface TrymigratePlugin {

    default void populate(TrymigrateBeanProvider beanProvider) {
    }

}
