package io.github.bekoenig.trymigrate.core.internal.plugin;

import java.util.function.Supplier;

public class PluginProvider implements Comparable<PluginProvider> {

    private final Class<?> type;
    private final Supplier<Object> factory;
    private final int rank;

    public PluginProvider(Class<?> type, Supplier<Object> factory, int rank) {
        this.type = type;
        this.factory = factory;
        this.rank = rank;
    }

    @Override
    public int compareTo(PluginProvider other) {
        return -Integer.compare(this.rank, other.rank);
    }

    public boolean isInstanceOf(Class<?> clazz) {
        return clazz.isAssignableFrom(type);
    }

    public Object provide() {
        return factory.get();
    }
}
