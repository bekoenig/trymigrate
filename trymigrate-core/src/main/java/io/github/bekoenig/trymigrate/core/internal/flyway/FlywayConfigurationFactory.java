package io.github.bekoenig.trymigrate.core.internal.flyway;

import io.github.bekoenig.trymigrate.core.TrymigrateTest;
import io.github.bekoenig.trymigrate.core.plugin.customize.TrymigrateFlywayCustomizer;
import io.github.bekoenig.trymigrate.core.internal.flyway.callback.SchemaLinter;
import org.flywaydb.core.api.callback.Callback;
import org.flywaydb.core.api.configuration.FluentConfiguration;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.github.bekoenig.trymigrate.core.plugin.customize.TrymigrateFlywayCustomizer.addCallbacks;

public class FlywayConfigurationFactory implements Supplier<FluentConfiguration> {

    private final Map<String, String> properties;

    private final TrymigrateFlywayCustomizer flywayConfigurerSupplier;

    private final Callback additionalCallback;

    public FlywayConfigurationFactory(
            String[] properties, TrymigrateFlywayCustomizer flywayConfigurerSupplier,
            SchemaLinter additionalCallback) {
        this.properties = splitProperties(properties);
        this.flywayConfigurerSupplier = flywayConfigurerSupplier;
        this.additionalCallback = additionalCallback;
    }

    private Map<String, String> splitProperties(String[] properties) {
        return Stream.of(properties)
                .map(property -> {
                    String[] tokens = property.split("=");
                    if (tokens.length != 2) {
                        throw new IllegalArgumentException("Property '%s' does not match format 'key=value'"
                                .formatted(property));
                    }
                    return tokens;
                })
                .collect(Collectors.toMap(
                        split -> normalizePrefix(split[0]),
                        split -> split[1]));
    }

    private String normalizePrefix(String propertyName) {
        if (propertyName.startsWith("flyway.")) {
            return propertyName;
        }

        return "flyway." + propertyName;
    }

    @Override
    public FluentConfiguration get() {
        FluentConfiguration configuration = new FluentConfiguration().configuration(properties);
        flywayConfigurerSupplier.accept(configuration);

        addCallbacks(configuration, List.of(additionalCallback));

        if (!configuration.getTarget().isPredefined()) {
            throw new UnsupportedOperationException("Forcing target for flyway is not allowed. Use @" +
                    TrymigrateTest.class.getSimpleName() + "#whenTarget to force target.");
        }

        return configuration;
    }

}
