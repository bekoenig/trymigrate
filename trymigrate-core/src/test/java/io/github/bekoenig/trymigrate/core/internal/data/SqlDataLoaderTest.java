package io.github.bekoenig.trymigrate.core.internal.data;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SqlDataLoaderTest {

    private final SqlDataLoader sqlDataLoader = new SqlDataLoader();

    @Test
    @DisplayName("GIVEN a sql extension WHEN checking support THEN return true")
    void shouldSupportSqlExtension() {
        // WHEN
        boolean result = sqlDataLoader.supports("some/resource.sql", "sql", null);

        // THEN
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("GIVEN an uppercase SQL extension WHEN checking support THEN return true")
    void shouldSupportUppercaseSqlExtension() {
        // WHEN
        boolean result = sqlDataLoader.supports("some/resource.SQL", "SQL", null);

        // THEN
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("GIVEN a non-sql extension WHEN checking support THEN return false")
    void shouldNotSupportOtherExtensions() {
        // WHEN
        boolean result = sqlDataLoader.supports("some/resource.txt", "txt", null);

        // THEN
        assertThat(result).isFalse();
    }

}
