package io.github.bekoenig.trymigrate.core.internal.jupiter.resolver;

import io.github.bekoenig.trymigrate.core.internal.jupiter.StoreSupport;
import org.junit.jupiter.api.extension.ExtensionContext;
import schemacrawler.tools.lint.Lints;

public class LintsParameterResolver extends AbstractParameterResolver<Lints> {

    @Override
    protected Class<Lints> forType() {
        return Lints.class;
    }

    @Override
    protected Lints currentValue(ExtensionContext extensionContext) {
        return StoreSupport.getLints(extensionContext);
    }

}
