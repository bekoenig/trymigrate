package io.github.bekoenig.trymigrate.core.internal.lifecycle;

import io.github.bekoenig.trymigrate.core.TrymigrateGivenData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MigrateExecutorTest {

    @Test
    @DisplayName("GIVEN @TrymigrateGivenData without @TrymigrateWhenTarget WHEN beforeEach THEN throw clear exception")
    void shouldFailOnGivenDataWithoutTarget() throws NoSuchMethodException {
        // GIVEN
        Method method = InvalidMigrationDataTestCase.class.getDeclaredMethod("invalid");
        ExtensionContext context = mock();
        when(context.getRequiredTestMethod()).thenReturn(method);

        // WHEN / THEN
        assertThatThrownBy(() -> new MigrateExecutor().beforeEach(context))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("@TrymigrateGivenData requires @TrymigrateWhenTarget");
    }

    static class InvalidMigrationDataTestCase {

        @TrymigrateGivenData("db/testdata/initial.sql")
        void invalid() {
        }
    }
}

