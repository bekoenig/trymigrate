package io.github.bekoenig.trymigrate.core.internal.plugin;

import io.github.bekoenig.trymigrate.core.plugin.TrymigratePlugin;
import io.github.bekoenig.trymigrate.core.plugin.TrymigratePluginProvider;

/**
 * Wrapper for {@link TrymigratePluginProvider} with some internal extensions.
 */
public class GenericPluginProvider {

    /**
     * Wrapped delegate.
     */
    private final TrymigratePluginProvider<TrymigratePlugin> delegate;

    /**
     * Rank of interface. See {@link TrymigratePlugin} for details.
     */
    private final Integer rank;

    public GenericPluginProvider(TrymigratePluginProvider<TrymigratePlugin> delegate, Integer rank) {
        this.delegate = delegate;
        this.rank = rank;
    }

    public TrymigratePlugin provide() {
        return delegate.provide();
    }

    public Integer getRank() {
        return rank;
    }

}
