package io.github.bekoenig.trymigrate.core.internal.database;

import io.github.bekoenig.trymigrate.core.plugin.customize.TrymigrateDatabase;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static io.github.bekoenig.trymigrate.core.internal.database.DatabaseDecorator.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class DatabaseDecoratorTest {

    private final TrymigrateDatabase delegate = mock(TrymigrateDatabase.class);

    @AfterEach
    void tearDown() {
        System.clearProperty(PROPERTY_NAME_LIFECYCLE_ENABLED);
        System.clearProperty(PROPERTY_NAME_URL);
        System.clearProperty(PROPERTY_NAME_USER);
        System.clearProperty(PROPERTY_NAME_PASSWORD);
    }

    @Test
    void shouldDelegateJdbcUrl() {
        DatabaseDecorator db = new DatabaseDecorator(delegate);
        when(delegate.getJdbcUrl()).thenReturn("jdbc:delegate");
        assertThat(db.getJdbcUrl()).isEqualTo("jdbc:delegate");
    }

    @Test
    void shouldOverrideJdbcUrl() {
        System.setProperty(PROPERTY_NAME_URL, "jdbc:overridden");
        DatabaseDecorator db = new DatabaseDecorator(delegate);
        assertThat(db.getJdbcUrl()).isEqualTo("jdbc:overridden");
    }

    @Test
    void shouldDelegateLifecycle() {
        DatabaseDecorator db = new DatabaseDecorator(delegate);
        db.prepare();
        verify(delegate).prepare();

        db.dispose();
        verify(delegate).dispose();
    }

    @Test
    void shouldNotDelegateLifecycleWhenDisabled() {
        System.setProperty(PROPERTY_NAME_LIFECYCLE_ENABLED, "false");
        DatabaseDecorator db = new DatabaseDecorator(delegate);

        db.prepare();
        verify(delegate, never()).prepare();

        db.dispose();
        verify(delegate, never()).dispose();
    }

    @Test
    void shouldBeDefinedWhenDelegateExists() {
        DatabaseDecorator db = new DatabaseDecorator(delegate);
        assertThat(db.isDefined()).isTrue();
    }

    @Test
    void shouldBeDefinedWhenPropertySet() {
        System.setProperty(PROPERTY_NAME_URL, "jdbc:prop");
        DatabaseDecorator db = new DatabaseDecorator(null);
        assertThat(db.isDefined()).isTrue();
    }

    @Test
    void shouldNotBeDefinedWhenNeitherExists() {
        DatabaseDecorator db = new DatabaseDecorator(null);
        assertThat(db.isDefined()).isFalse();
    }

}
