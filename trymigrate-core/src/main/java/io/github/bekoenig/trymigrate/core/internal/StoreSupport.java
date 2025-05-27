package io.github.bekoenig.trymigrate.core.internal;

import io.github.bekoenig.trymigrate.core.Trymigrate;
import io.github.bekoenig.trymigrate.core.internal.migrate.MigrateProcessor;
import org.junit.jupiter.api.extension.ExtensionContext;

public class StoreSupport {

    private static final String MIGRATE_PROCESSOR = "migrate-processor";

    private StoreSupport() {
    }

    private static ExtensionContext.Store getStore(ExtensionContext extensionContext) {
        return extensionContext.getStore(ExtensionContext.Namespace.create(Trymigrate.class,
                extensionContext.getRequiredTestClass()));
    }

    public static MigrateProcessor getMigrateProcessor(ExtensionContext extensionContext) {
        return getStore(extensionContext).get(MIGRATE_PROCESSOR, MigrateProcessor.class);
    }

    public static void putMigrateProcessor(ExtensionContext extensionContext, MigrateProcessor migrateProcessor) {
        getStore(extensionContext).put(MIGRATE_PROCESSOR, migrateProcessor);
    }
}
