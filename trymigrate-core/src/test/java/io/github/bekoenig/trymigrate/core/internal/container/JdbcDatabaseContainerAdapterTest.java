package io.github.bekoenig.trymigrate.core.internal.container;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.JdbcDatabaseContainer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class JdbcDatabaseContainerAdapterTest {

    @Test
    @DisplayName("GIVEN a non-shared container WHEN disposed THEN container is stopped")
    void shouldStopNonSharedContainer() {
        // GIVEN
        JdbcDatabaseContainer<?> container = mock();
        JdbcDatabaseContainerAdapter adapter = new JdbcDatabaseContainerAdapter(container, false);

        // WHEN
        adapter.dispose();

        // THEN
        verify(container).stop();
    }

    @Test
    @DisplayName("GIVEN a shared container WHEN disposed THEN container is NOT stopped")
    void shouldNotStopSharedContainer() {
        // GIVEN
        JdbcDatabaseContainer<?> container = mock();
        JdbcDatabaseContainerAdapter adapter = new JdbcDatabaseContainerAdapter(container, true);

        // WHEN
        adapter.dispose();

        // THEN
        verify(container, never()).stop();
    }

    @Test
    @DisplayName("GIVEN a container WHEN unwrapping same type THEN return container")
    void shouldUnwrapContainer() {
        // GIVEN
        JdbcDatabaseContainer<?> container = mock();
        JdbcDatabaseContainerAdapter adapter = new JdbcDatabaseContainerAdapter(container, false);

        // WHEN
        var result = adapter.unwrap(JdbcDatabaseContainer.class);

        // THEN
        assertThat(result).contains(container);
    }

    @Test
    @DisplayName("GIVEN a container WHEN getting connection info THEN return container info")
    void shouldReturnContainerConnectionInfo() {
        // GIVEN
        JdbcDatabaseContainer<?> container = mock();
        when(container.getJdbcUrl()).thenReturn("jdbc:mock");
        when(container.getUsername()).thenReturn("user");
        when(container.getPassword()).thenReturn("pass");
        JdbcDatabaseContainerAdapter adapter = new JdbcDatabaseContainerAdapter(container, false);

        // THEN
        assertThat(adapter.getJdbcUrl()).isEqualTo("jdbc:mock");
        assertThat(adapter.getUsername()).isEqualTo("user");
        assertThat(adapter.getPassword()).isEqualTo("pass");
    }

}
