package io.github.bekoenig.trymigrate.core;

import cr.Classpath;
import io.github.bekoenig.trymigrate.core.plugin.TrymigrateRegisterPlugin;
import io.github.bekoenig.trymigrate.core.plugin.customize.TrymigrateFlywayCustomizer;
import org.junit.jupiter.api.Test;

@Trymigrate
@Classpath(exclude = "org.testcontainers:testcontainers-jdbc", excludeTransitive = true)
public class TrymigrateH2TestcontainersExcludedTest {

    @TrymigrateRegisterPlugin
    private final TrymigrateFlywayCustomizer flywayCustomizer = configuration -> configuration
            .defaultSchema("EXAMPLE_SCHEMA")
            .dataSource("jdbc:h2:mem:testdb2;DB_CLOSE_DELAY=-1", null, null)
            .locations("classpath:db/migration/example/h2");

    @Test
    @TrymigrateWhenTarget("1.0")
    void initial() {
    }

}
