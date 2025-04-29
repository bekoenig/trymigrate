package io.github.bekoenig.trymigrate.core.internal.bean;

import io.github.bekoenig.trymigrate.core.config.TrymigratePlugin;

import java.util.function.Supplier;

public class PluginProvider {

    private final Supplier<TrymigratePlugin> factory;
    private final Integer priority;

    public PluginProvider(Supplier<TrymigratePlugin> factory, Integer priority) {
        this.factory = factory;
        this.priority = priority;
    }

    public TrymigratePlugin newInstance() {
        return factory.get();
    }

    public Integer getPriority() {
        return priority;
    }

}
