package io.github.bekoenig.trymigrate.core.internal.database.container;

import io.github.bekoenig.trymigrate.core.internal.database.container.StaticPortBinding;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junitpioneer.jupiter.ClearSystemProperty;
import org.junitpioneer.jupiter.SetSystemProperty;
import org.testcontainers.containers.JdbcDatabaseContainer;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class StaticPortBindingTest {

    private final StaticPortBinding customizer = new StaticPortBinding();

    @Test
    @DisplayName("GIVEN no port property WHEN accepting container THEN do nothing")
    @ClearSystemProperty(key = StaticPortBinding.PROPERTY_NAME)
    void accept_onUndefined() {
        // GIVEN
        JdbcDatabaseContainer<?> container = mock();

        // WHEN
        customizer.accept(container);

        // THEN
        verify(container, times(0)).setExposedPorts(any());
    }

    @Test
    @DisplayName("GIVEN a host port property WHEN accepting container THEN bind host port to single exposed port")
    @SetSystemProperty(key = StaticPortBinding.PROPERTY_NAME, value = "20000")
    void accept_hostPort() {
        // GIVEN
        JdbcDatabaseContainer<?> container = mock();
        when(container.getExposedPorts()).thenReturn(List.of(40000));

        // WHEN
        customizer.accept(container);

        // THEN
        verify(container).setPortBindings(List.of("20000:40000"));
    }

    @Test
    @DisplayName("GIVEN a combined port property WHEN accepting container THEN bind specified host and container ports")
    @SetSystemProperty(key = StaticPortBinding.PROPERTY_NAME, value = "20000:40000")
    void accept_hostAndContainerPort() {
        // GIVEN
        JdbcDatabaseContainer<?> container = mock();

        // WHEN
        customizer.accept(container);

        // THEN
        verify(container).setPortBindings(List.of("20000:40000"));
    }

    @Test
    @DisplayName("GIVEN an invalid port property WHEN accepting container THEN throw IllegalArgumentException")
    @SetSystemProperty(key = StaticPortBinding.PROPERTY_NAME, value = "abc:20000")
    void accept_failOnWrongFormat() {
        // GIVEN
        JdbcDatabaseContainer<?> container = mock();

        // WHEN
        assertThatThrownBy(() -> customizer.accept(container)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("GIVEN a host port property but no container port WHEN accepting container THEN throw IllegalStateException")
    @SetSystemProperty(key = StaticPortBinding.PROPERTY_NAME, value = "40000")
    void accept_failOnNonExposedPort() {
        // GIVEN
        JdbcDatabaseContainer<?> container = mock();
        when(container.getExposedPorts()).thenReturn(List.of());

        // WHEN
        assertThatThrownBy(() -> customizer.accept(container)).isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("GIVEN a host port property but multiple container ports WHEN accepting container THEN throw IllegalStateException due to ambiguity")
    @SetSystemProperty(key = StaticPortBinding.PROPERTY_NAME, value = "40000")
    void accept_failOnMultipleExposedPort() {
        // GIVEN
        JdbcDatabaseContainer<?> container = mock();
        when(container.getExposedPorts()).thenReturn(List.of(20000, 20001));

        // WHEN
        assertThatThrownBy(() -> customizer.accept(container)).isInstanceOf(IllegalStateException.class);
    }
}
