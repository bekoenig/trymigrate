package io.github.bekoenig.trymigrate.core.internal.lifecycle;

import io.github.bekoenig.trymigrate.core.internal.StoreSupport;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestInstancePreDestroyCallback;

public class MigrateCompleter implements TestInstancePreDestroyCallback {

    @Override
    public void preDestroyTestInstance(ExtensionContext extensionContext) {
        StoreSupport.getMigrateProcessor(extensionContext).finish();
    }

}