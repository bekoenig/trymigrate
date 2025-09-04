package io.github.bekoenig.trymigrate.core.internal.plugin;

import io.github.bekoenig.trymigrate.core.plugin.TrymigrateBeanProvider;
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
     * Hierarchy of interface. See {@link TrymigratePlugin} for details.
     */
    private final Integer hierarchy;

    public GenericPluginProvider(TrymigratePluginProvider<TrymigratePlugin> delegate, Integer hierarchy) {
        this.delegate = delegate;
        this.hierarchy = hierarchy;
    }

    public TrymigratePlugin provide(TrymigrateBeanProvider beanProvider) {
        return delegate.provide(beanProvider);
    }

    public Integer getHierarchy() {
        return hierarchy;
    }

}
