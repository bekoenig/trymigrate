package io.github.bekoenig.trymigrate.core.internal.bean;

import io.github.bekoenig.trymigrate.core.config.TrymigratePlugin;

import java.text.MessageFormat;
import java.util.Iterator;
import java.util.ServiceLoader;
import java.util.stream.Collectors;

public class PluginServiceLoader implements TrymigratePlugin {

    public TrymigratePlugin get() {
        ServiceLoader<TrymigratePlugin> serviceLoader = ServiceLoader.load(TrymigratePlugin.class);

        Iterator<TrymigratePlugin> iterator = serviceLoader.iterator();
        if (!iterator.hasNext()) {
            return new TrymigratePlugin() {
            };
        }

        TrymigratePlugin instance = iterator.next();
        if (iterator.hasNext()) {
            throw new IllegalStateException(MessageFormat.format(
                    "Found multiple plugins in classpath. Select a single one using @Trymigrate#plugin:{0}{1}",
                    System.lineSeparator(),
                    serviceLoader.stream()
                            .map(x -> x.getClass().getName())
                            .collect(Collectors.joining(System.lineSeparator()))));
        }

        return instance;
    }

}
