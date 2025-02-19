package io.github.bekoenig.trymigrate.core.internal.jupiter.extension;

import io.github.bekoenig.trymigrate.core.internal.flyway.FlywayConfigurationFactory;
import io.github.bekoenig.trymigrate.core.internal.flyway.FlywayMigrateWrapper;
import io.github.bekoenig.trymigrate.core.internal.jupiter.StoreSupport;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestInstancePreDestroyCallback;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.lifecycle.Startable;

import java.util.List;

public class MigrateCompleter implements TestInstancePreDestroyCallback {

    @Override
    public void preDestroyTestInstance(ExtensionContext extensionContext) {
        FlywayConfigurationFactory flywayConfigurationFactory = StoreSupport.getFlywayConfigurationFactory(extensionContext);
        if (flywayConfigurationFactory != null) {
            FlywayMigrateWrapper flywayMigrateWrapper = new FlywayMigrateWrapper(extensionContext);
            if (!flywayMigrateWrapper.isLatest()) {
                flywayMigrateWrapper.migrate(flywayConfigurationFactory.get().load(), List.of());
            }
        }

        StoreSupport.getBeanProvider(extensionContext)
                .findOne(JdbcDatabaseContainer.class)
                .ifPresent(Startable::stop);
    }

}