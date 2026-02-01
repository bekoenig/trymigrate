package io.github.bekoenig.trymigrate.core.internal.plugin;

import io.github.bekoenig.trymigrate.core.plugin.TrymigratePlugin;
import io.github.bekoenig.trymigrate.core.plugin.customize.*;

public class PluginTypesValidator {

    private PluginTypesValidator() {
    }

    public static boolean isSupportedType(Class<?> type) {
        return TrymigratePlugin.SUPPORTED_TYPES.stream()
                .anyMatch(supportedType -> supportedType.isAssignableFrom(type));
    }

}
