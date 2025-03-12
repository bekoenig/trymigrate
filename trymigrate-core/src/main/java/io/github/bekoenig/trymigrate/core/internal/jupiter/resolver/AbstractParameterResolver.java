package io.github.bekoenig.trymigrate.core.internal.jupiter.resolver;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolver;

import java.util.Objects;

public abstract class AbstractParameterResolver<T> implements ParameterResolver {

    protected abstract Class<T> forType();

    protected abstract T currentValue(ExtensionContext extensionContext);

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        return parameterContext.getParameter().getType().equals(forType())
                && Objects.nonNull(currentValue(extensionContext));
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        return currentValue(extensionContext);
    }

}
