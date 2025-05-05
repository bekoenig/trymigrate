package io.github.bekoenig.trymigrate.core.internal.testcontainers;

import org.junit.jupiter.api.Test;
import org.junitpioneer.jupiter.ClearSystemProperty;
import org.junitpioneer.jupiter.SetSystemProperty;
import org.testcontainers.containers.JdbcDatabaseContainer;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class ContainerPortCustomizerTest {

    private final ContainerPortCustomizer customizer = new ContainerPortCustomizer();

    @Test
    @ClearSystemProperty(key = ContainerPortCustomizer.PROPERTY_NAME)
    void accept_onUndefined() {
        // GIVEN
        JdbcDatabaseContainer<?> container = mock();

        // WHEN
        customizer.accept(container);

        // THEN
        verify(container, times(0)).setExposedPorts(any());
    }

    @Test
    @SetSystemProperty(key = ContainerPortCustomizer.PROPERTY_NAME, value = "20000")
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
    @SetSystemProperty(key = ContainerPortCustomizer.PROPERTY_NAME, value = "20000:40000")
    void accept_hostAndContainerPort() {
        // GIVEN
        JdbcDatabaseContainer<?> container = mock();

        // WHEN
        customizer.accept(container);

        // THEN
        verify(container).setPortBindings(List.of("20000:40000"));
    }

    @Test
    @SetSystemProperty(key = ContainerPortCustomizer.PROPERTY_NAME, value = "abc:20000")
    void accept_failOnWrongFormat() {
        // GIVEN
        JdbcDatabaseContainer<?> container = mock();

        // WHEN
        assertThatThrownBy(() -> customizer.accept(container)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @SetSystemProperty(key = ContainerPortCustomizer.PROPERTY_NAME, value = "40000")
    void accept_failOnNonExposedPort() {
        // GIVEN
        JdbcDatabaseContainer<?> container = mock();
        when(container.getExposedPorts()).thenReturn(List.of());

        // WHEN
        assertThatThrownBy(() -> customizer.accept(container)).isInstanceOf(IllegalStateException.class);
    }

    @Test
    @SetSystemProperty(key = ContainerPortCustomizer.PROPERTY_NAME, value = "40000")
    void accept_failOnMultipleExposedPort() {
        // GIVEN
        JdbcDatabaseContainer<?> container = mock();
        when(container.getExposedPorts()).thenReturn(List.of(20000, 20001));

        // WHEN
        assertThatThrownBy(() -> customizer.accept(container)).isInstanceOf(IllegalStateException.class);
    }
}