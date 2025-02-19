package io.github.bekoenig.trymigrate.core.internal.jupiter.resolver;

import io.github.bekoenig.trymigrate.core.internal.jupiter.StoreSupport;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolver;
import schemacrawler.tools.lint.Lints;

import java.util.Objects;

public class LintsParameterResolver implements ParameterResolver {

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        return parameterContext.getParameter().getType().equals(Lints.class)
                && Objects.nonNull(StoreSupport.getLints(extensionContext));
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        return StoreSupport.getLints(extensionContext);
    }

}
