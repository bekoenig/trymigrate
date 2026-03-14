package io.github.bekoenig.trymigrate.core.internal.catalog;

import io.github.bekoenig.trymigrate.core.plugin.customize.TrymigrateCatalogCustomizer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import schemacrawler.schema.Catalog;
import schemacrawler.schemacrawler.LimitOptionsBuilder;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class CatalogFactoryTest {

    @Test
    @DisplayName("GIVEN a connection WHEN crawling THEN return catalog AND apply customizers")
    void shouldCrawlAndApplyCustomizers() throws SQLException {
        // GIVEN
        TrymigrateCatalogCustomizer customizer = mock(TrymigrateCatalogCustomizer.class);
        CatalogFactory factory = new CatalogFactory(List.of(customizer));

        try (Connection connection = DriverManager.getConnection("jdbc:h2:mem:catalog_test")) {
            connection.createStatement().execute("CREATE TABLE test (id INT)");

            // WHEN
            Catalog catalog = factory.crawl(connection, Set.of("PUBLIC"));

            // THEN
            assertThat(catalog).isNotNull();
            assertThat(catalog.getTables()).anyMatch(t -> t.getName().equalsIgnoreCase("test"));

            verify(customizer).customize(any(LimitOptionsBuilder.class));
        }
    }

}
