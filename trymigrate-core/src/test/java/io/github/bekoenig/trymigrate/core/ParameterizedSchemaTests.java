package io.github.bekoenig.trymigrate.core;

import io.github.bekoenig.assertj.schemacrawler.api.SchemaCrawlerAssertions;
import io.github.bekoenig.trymigrate.core.plugin.TrymigrateRegisterPlugin;
import io.github.bekoenig.trymigrate.core.plugin.customize.TrymigrateFlywayCustomizer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.Parameter;
import org.junit.jupiter.params.ParameterizedClass;
import org.junit.jupiter.params.provider.CsvSource;
import schemacrawler.schema.Catalog;


@ParameterizedClass
@CsvSource({
        "SCHEMA1,classpath:db/migration/example/h2",
        "SCHEMA2,classpath:db/migration/example/h2"
})
public class ParameterizedSchemaTests {

    @Parameter(0)
    String defaultSchema;
    @Parameter(1)
    String location;

    @Trymigrate
    @Nested
    class SchemaTest {

        @TrymigrateRegisterPlugin
        private final TrymigrateFlywayCustomizer flywayCustomizer = configuration -> configuration
                .defaultSchema(defaultSchema)
                .dataSource("jdbc:h2:mem:testdb1;DB_CLOSE_DELAY=-1", null, null)
                .locations(location);

        @Test
        @TrymigrateWhenTarget("latest")
        void latest(Catalog catalog) {
            SchemaCrawlerAssertions.assertThat(catalog)
                    .schema("TESTDB1." + defaultSchema)
                    .isNotNull();
        }
    }
}
