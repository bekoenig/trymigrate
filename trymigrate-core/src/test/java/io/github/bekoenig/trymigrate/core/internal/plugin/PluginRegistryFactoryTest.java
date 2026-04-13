package io.github.bekoenig.trymigrate.core.internal.plugin;

import io.github.bekoenig.trymigrate.core.internal.database.container.JdbcDatabaseContainerAdapter;
import io.github.bekoenig.trymigrate.core.plugin.TrymigrateRegisterPlugin;
import io.github.bekoenig.trymigrate.core.plugin.customize.TrymigrateDataLoader;
import io.github.bekoenig.trymigrate.core.plugin.customize.TrymigrateDatabase;
import io.github.bekoenig.trymigrate.core.plugin.customize.TrymigrateFlywayCustomizer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

class PluginRegistryFactoryTest {

    private final PluginRegistryFactory factory = new PluginRegistryFactory();

    @Test
    @DisplayName("GIVEN a test instance with plugins WHEN creating registry THEN return registry with field plugins")
    void shouldCreateRegistryWithFieldPlugins() {
        // GIVEN
        TestWithPlugins testInstance = new TestWithPlugins();
        testInstance.loader = mock(TrymigrateDataLoader.class);
        testInstance.flyway = config -> {
        };

        // WHEN
        PluginRegistry registry = factory.create(testInstance, List.of());

        // THEN
        assertThat(registry.all(TrymigrateDataLoader.class)).containsExactly(testInstance.loader);
        assertThat(registry.all(TrymigrateFlywayCustomizer.class)).containsExactly(testInstance.flyway);
    }

    @Test
    @DisplayName("GIVEN an unsupported plugin field WHEN creating registry THEN throw exception")
    void shouldThrowForUnsupportedField() {
        // GIVEN
        TestWithUnsupportedPlugin testInstance = new TestWithUnsupportedPlugin();
        testInstance.unsupported = "not a plugin";

        // WHEN / THEN
        assertThatThrownBy(() -> factory.create(testInstance, List.of()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("unsupported type");
    }

    @Test
    @DisplayName("GIVEN a null plugin field WHEN getting all THEN throw exception")
    void shouldThrowForNullField() {
        // GIVEN
        TestWithPlugins testInstance = new TestWithPlugins();
        testInstance.loader = null; // null field
        PluginRegistry registry = factory.create(testInstance, List.of());

        // WHEN / THEN
        assertThatThrownBy(() -> registry.all(TrymigrateDataLoader.class))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("is null");
    }

    @Test
    @DisplayName("GIVEN a JdbcDatabaseContainer field WHEN creating registry THEN wrap in adapter")
    void shouldWrapJdbcDatabaseContainer() {
        // GIVEN
        TestWithContainer testInstance = new TestWithContainer();
        testInstance.container = mock(org.testcontainers.containers.JdbcDatabaseContainer.class);

        // WHEN
        PluginRegistry registry = factory.create(testInstance, List.of());

        // THEN
        // Note: We don't check for TrymigrateDatabaseOverride here because PluginRegistryFactory
        // is just the factory for the PluginProviders. Wrapping happens in PluginRegistry.
        List<TrymigrateDatabase> databases = registry.all(TrymigrateDatabase.class);
        assertThat(databases).hasSize(1);
        assertThat(databases.get(0)).isInstanceOf(JdbcDatabaseContainerAdapter.class);
    }

    static class TestWithContainer {
        @TrymigrateRegisterPlugin
        org.testcontainers.containers.JdbcDatabaseContainer<?> container;
    }

    static class TestWithPlugins {
        @TrymigrateRegisterPlugin
        TrymigrateDataLoader loader;

        @TrymigrateRegisterPlugin
        TrymigrateFlywayCustomizer flyway;
    }

    static class TestWithUnsupportedPlugin {
        @TrymigrateRegisterPlugin
        String unsupported;
    }

}
