package io.github.bekoenig.trymigrate.core.internal.bean;

import io.github.bekoenig.trymigrate.core.plugin.TrymigratePlugin;

import java.util.function.Supplier;

public class PluginProvider {

    private final Supplier<TrymigratePlugin> factory;
    private final Integer hierarchy;

    public PluginProvider(Supplier<TrymigratePlugin> factory, Integer hierarchy) {
        this.factory = factory;
        this.hierarchy = hierarchy;
    }

    public TrymigratePlugin newInstance() {
        return factory.get();
    }

    public Integer getHierarchy() {
        return hierarchy;
    }

}
