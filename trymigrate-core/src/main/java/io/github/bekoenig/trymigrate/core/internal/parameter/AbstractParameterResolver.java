package io.github.bekoenig.trymigrate.core.internal.parameter;

import io.github.bekoenig.trymigrate.core.TrymigrateTest;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.platform.commons.support.AnnotationSupport;

import java.util.Objects;

public abstract class AbstractParameterResolver<T> implements ParameterResolver {

    protected abstract Class<T> forType();

    protected abstract T currentValue(ExtensionContext extensionContext);

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        return AnnotationSupport.isAnnotated(extensionContext.getRequiredTestMethod(), TrymigrateTest.class)
                && parameterContext.getParameter().getType().equals(forType())
                && Objects.nonNull(currentValue(extensionContext));
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        return currentValue(extensionContext);
    }

}
