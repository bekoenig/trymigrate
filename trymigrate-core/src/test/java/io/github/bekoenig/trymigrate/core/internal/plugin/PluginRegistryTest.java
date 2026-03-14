package io.github.bekoenig.trymigrate.core.internal.plugin;

import io.github.bekoenig.trymigrate.core.plugin.customize.TrymigrateDataLoader;
import io.github.bekoenig.trymigrate.core.plugin.customize.TrymigrateFlywayCustomizer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

class PluginRegistryTest {

    @Test
    @DisplayName("GIVEN multiple providers WHEN getting all THEN return in registered order")
    void shouldReturnAllPlugins() {
        // GIVEN
        TrymigrateDataLoader loader1 = mock();
        TrymigrateDataLoader loader2 = mock();
        PluginProvider p1 = new PluginProvider(TrymigrateDataLoader.class, () -> loader1, 1);
        PluginProvider p2 = new PluginProvider(TrymigrateDataLoader.class, () -> loader2, 2);
        PluginRegistry registry = new PluginRegistry(List.of(p1, p2));

        // WHEN
        List<TrymigrateDataLoader> result = registry.all(TrymigrateDataLoader.class);

        // THEN
        assertThat(result).containsExactly(loader1, loader2);
    }

    @Test
    @DisplayName("GIVEN multiple providers WHEN getting reserved order THEN return in reverse")
    void shouldReturnAllPluginsInReverse() {
        // GIVEN
        TrymigrateDataLoader loader1 = mock();
        TrymigrateDataLoader loader2 = mock();
        PluginProvider p1 = new PluginProvider(TrymigrateDataLoader.class, () -> loader1, 1);
        PluginProvider p2 = new PluginProvider(TrymigrateDataLoader.class, () -> loader2, 2);
        PluginRegistry registry = new PluginRegistry(List.of(p1, p2));

        // WHEN
        List<TrymigrateDataLoader> result = registry.allReservedOrder(TrymigrateDataLoader.class);

        // THEN
        assertThat(result).containsExactly(loader2, loader1);
    }

    @Test
    @DisplayName("GIVEN one provider WHEN finding one THEN return optional with plugin")
    void shouldFindOnePlugin() {
        // GIVEN
        TrymigrateFlywayCustomizer customizer = mock();
        PluginProvider p1 = new PluginProvider(TrymigrateFlywayCustomizer.class, () -> customizer, 1);
        PluginRegistry registry = new PluginRegistry(List.of(p1));

        // WHEN
        var result = registry.findOne(TrymigrateFlywayCustomizer.class);

        // THEN
        assertThat(result).contains(customizer);
    }

    @Test
    @DisplayName("GIVEN multiple providers WHEN finding one THEN throw exception")
    void shouldThrowIfMultiplePluginsFound() {
        // GIVEN
        TrymigrateFlywayCustomizer c1 = mock();
        TrymigrateFlywayCustomizer c2 = mock();
        PluginProvider p1 = new PluginProvider(TrymigrateFlywayCustomizer.class, () -> c1, 1);
        PluginProvider p2 = new PluginProvider(TrymigrateFlywayCustomizer.class, () -> c2, 2);
        PluginRegistry registry = new PluginRegistry(List.of(p1, p2));

        // WHEN / THEN
        assertThatThrownBy(() -> registry.findOne(TrymigrateFlywayCustomizer.class))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Multiple plugins for type");
    }

    @Test
    @DisplayName("GIVEN unsupported type WHEN streaming THEN throw exception")
    void shouldThrowForUnsupportedType() {
        // GIVEN
        PluginRegistry registry = new PluginRegistry(List.of());

        // WHEN / THEN
        assertThatThrownBy(() -> registry.all(String.class))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Unsupported plugin type");
    }

}
