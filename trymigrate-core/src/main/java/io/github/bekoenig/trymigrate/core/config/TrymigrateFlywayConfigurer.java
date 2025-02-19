package io.github.bekoenig.trymigrate.core.config;

import org.flywaydb.core.api.callback.Callback;
import org.flywaydb.core.api.configuration.FluentConfiguration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public interface TrymigrateFlywayConfigurer extends Consumer<FluentConfiguration> {

    static void addCallbacks(FluentConfiguration configuration, List<Callback> callback) {
        List<Callback> callbacks = new ArrayList<>();
        if (configuration.getCallbacks() != null) {
            Collections.addAll(callbacks, configuration.getCallbacks());
        }
        callbacks.addAll(callback);
        configuration.callbacks(callbacks.toArray(new Callback[0]));
    }

}
