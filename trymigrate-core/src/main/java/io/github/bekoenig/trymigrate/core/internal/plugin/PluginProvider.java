package io.github.bekoenig.trymigrate.core.internal.plugin;

import java.util.function.Supplier;

/**
 * Internal provider for a plugin instance.
 *
 * @param type    the specific type of the plugin
 * @param factory the factory to create the plugin instance
 * @param rank    the rank for prioritization (higher rank = higher priority)
 */
public record PluginProvider(Class<?> type, Supplier<Object> factory, int rank) implements Comparable<PluginProvider> {

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
