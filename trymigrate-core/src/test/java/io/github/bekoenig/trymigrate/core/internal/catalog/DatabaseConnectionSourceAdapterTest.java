package io.github.bekoenig.trymigrate.core.internal.catalog;

import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class DatabaseConnectionSourceAdapterTest {

    @Test
    void get() {
        // GIVEN
        Connection mock = mock();
        DatabaseConnectionSourceAdapter adapter = new DatabaseConnectionSourceAdapter(mock);

        // WHEN
        Connection connection = adapter.get();

        // THEN
        assertThat(connection)
                .isEqualTo(mock)
                .isNotSameAs(mock);
    }

    @Test
    void closePreventingProxy() throws SQLException {
        Connection mock = mock();
        Connection proxy = DatabaseConnectionSourceAdapter.closePreventingProxy(mock);

        // prevents close
        proxy.close();
        verify(mock, never()).close();

        // delegates other methods
        proxy.commit();
        verify(mock, times(1)).commit();

        proxy.rollback();
        verify(mock, times(1)).rollback();
    }

}