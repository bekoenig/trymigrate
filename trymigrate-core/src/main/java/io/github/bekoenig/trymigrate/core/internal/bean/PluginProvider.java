package io.github.bekoenig.trymigrate.core.internal.bean;

import io.github.bekoenig.trymigrate.core.config.TrymigratePlugin;

import java.util.Objects;
import java.util.function.Supplier;

public class PluginProvider implements Supplier<TrymigratePlugin> {

    private final Class<? extends TrymigratePlugin> type;
    private final Supplier<TrymigratePlugin> supplier;

    public PluginProvider(Class<? extends TrymigratePlugin> type, Supplier<TrymigratePlugin> supplier) {
        this.type = type;
        this.supplier = supplier;
    }

    @Override
    public TrymigratePlugin get() {
        return supplier.get();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return Objects.equals(type, ((PluginProvider) o).type);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(type);
    }
}
