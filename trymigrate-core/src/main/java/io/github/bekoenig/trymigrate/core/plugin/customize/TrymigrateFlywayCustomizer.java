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
 * Customizer for trymigrate's Flyway configuration.
 * <p>
 * This interface allows you to fine-tune Flyway's behavior, such as:
 * <ul>
 *     <li>Specifying custom migration locations (e.g., {@code .locations("classpath:db/migration")}).</li>
 *     <li>Defining the default schema and additional schemas to manage.</li>
 *     <li>Enabling or disabling specific Flyway features (like {@code clean}).</li>
 *     <li>Adding custom Flyway placeholders or configurations.</li>
 * </ul>
 * <p>
 * <b>Registration:</b>
 * Register an implementation of this interface via {@link io.github.bekoenig.trymigrate.core.plugin.TrymigrateRegisterPlugin}
 * or Java SPI.
 * <p>
 * <b>Example:</b>
 * <pre>{@code
 * @TrymigrateRegisterPlugin
 * private final TrymigrateFlywayCustomizer customizer = config -> config
 *     .schemas("CORE", "AUDIT")
 *     .cleanDisabled(false)
 *     .placeholderReplacement(true);
 * }</pre>
 *
 * @see io.github.bekoenig.trymigrate.core.plugin.TrymigrateRegisterPlugin
 * @see org.flywaydb.core.api.configuration.FluentConfiguration
 */
public interface TrymigrateFlywayCustomizer extends Consumer<FluentConfiguration> {

    /**
     * Utility method to append values to an existing Flyway configuration array.
     *
     * @param <T>     the type of the configuration elements
     * @param getter  supplier for the current array
     * @param values  collection of values to add
     * @param factory factory to create the new array
     * @param setter  consumer to set the new array
     */
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

    /**
     * Appends Java-based migrations to the current Flyway configuration.
     *
     * @param configuration  the Flyway configuration to modify
     * @param javaMigrations the migrations to add
     */
    static void addJavaMigrations(FluentConfiguration configuration, List<JavaMigration> javaMigrations) {
        add(configuration::getJavaMigrations, javaMigrations, JavaMigration[]::new, configuration::javaMigrations);
    }

    /**
     * Appends callbacks to the current Flyway configuration.
     *
     * @param configuration the Flyway configuration to modify
     * @param callbacks     the callbacks to add
     */
    static void addCallbacks(FluentConfiguration configuration, List<Callback> callbacks) {
        add(configuration::getCallbacks, callbacks, Callback[]::new, configuration::callbacks);
    }

}
