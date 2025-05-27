package io.github.bekoenig.trymigrate.database.postgresql;

import io.github.bekoenig.trymigrate.core.Trymigrate;
import io.github.bekoenig.trymigrate.core.TrymigrateTest;
import io.github.bekoenig.trymigrate.core.internal.StoreSupport;
import io.github.bekoenig.trymigrate.core.internal.lint.LintPatterns;
import io.github.bekoenig.trymigrate.core.internal.migrate.MigrateProcessor;
import io.github.bekoenig.trymigrate.core.lint.TrymigrateExcludeLint;
import io.github.bekoenig.trymigrate.core.plugin.TrymigrateBean;
import org.flywaydb.core.api.MigrationVersion;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestInstancePostProcessor;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Trymigrate(
        flywayProperties = {
                "defaultSchema=example_schema",
                "locations=classpath:db/migration/example/postgresql"
        })
@TrymigrateExcludeLint
@ExtendWith({
        FailOnAboveTargetTest.class
})
public class FailOnAboveTargetTest implements TestInstancePostProcessor {

    @TrymigrateBean
    private final PostgreSQLContainer<?> container = new PostgreSQLContainer<>(DockerImageName.parse("postgres:17.2"));

    private static ExtensionContext extensionContext;

    @Override
    public void postProcessTestInstance(Object testInstance, ExtensionContext extensionContext) {
        FailOnAboveTargetTest.extensionContext = extensionContext;
    }

    @TrymigrateTest(whenTarget = "1.1")
    @Order(1)
    void migrate() {
        // GIVEN
        MigrateProcessor migrateProcessor = StoreSupport.getMigrateProcessor(extensionContext);
        MigrationVersion target = MigrationVersion.fromVersion("1.0");

        // WHEN & THEN
        assertThatThrownBy(() -> migrateProcessor.migrate(target, List.of(), false, LintPatterns.EMPTY))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Schema version 1.1 is newer than target 1.0 for test.");
    }

}
