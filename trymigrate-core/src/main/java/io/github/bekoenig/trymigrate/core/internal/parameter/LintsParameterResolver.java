package io.github.bekoenig.trymigrate.core.internal.parameter;

import io.github.bekoenig.trymigrate.core.internal.StoreSupport;
import org.junit.jupiter.api.extension.ExtensionContext;
import schemacrawler.tools.lint.Lints;

public class LintsParameterResolver extends AbstractParameterResolver<Lints> {

    @Override
    protected Class<Lints> forType() {
        return Lints.class;
    }

    @Override
    protected Lints currentValue(ExtensionContext extensionContext) {
        return StoreSupport.getLintsHistory(extensionContext).get(StoreSupport.getMigrationVersion(extensionContext));
    }

}
