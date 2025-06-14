package io.github.bekoenig.trymigrate.core.plugin.customize;

import org.flywaydb.core.api.callback.Callback;
import org.flywaydb.core.api.configuration.FluentConfiguration;
import org.flywaydb.core.api.migration.JavaMigration;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Customizer for additional configurations on flyway using {@link FluentConfiguration}.
 */
public interface TrymigrateFlywayCustomizer extends Consumer<FluentConfiguration> {

    static <T> void add(Supplier<T[]> getter, Collection<T> values, Function<Integer, T[]> factory,
                        Consumer<T[]> setter) {
        if (values.isEmpty()) {
            return;
        }

        T[] current = getter.get();
        T[] result = factory.apply(current.length + values.size());
        System.arraycopy(current, 0, result, 0, current.length);
        System.arraycopy(values.toArray(), 0, result, current.length, values.size());
        setter.accept(result);
    }

    static void addJavaMigrations(FluentConfiguration configuration, List<JavaMigration> javaMigrations) {
        add(configuration::getJavaMigrations, javaMigrations, JavaMigration[]::new, configuration::javaMigrations);
    }

    static void addCallbacks(FluentConfiguration configuration, List<Callback> callbacks) {
        add(configuration::getCallbacks, callbacks, Callback[]::new, configuration::callbacks);
    }

}
