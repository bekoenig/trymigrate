package io.github.bekoenig.trymigrate.core.internal.jupiter.resolver;

import io.github.bekoenig.trymigrate.core.internal.jupiter.StoreSupport;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolver;
import schemacrawler.schema.Catalog;

import java.util.Objects;

public class CatalogParameterResolver implements ParameterResolver {

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        return parameterContext.getParameter().getType().equals(Catalog.class)
                && Objects.nonNull(StoreSupport.getCatalog(extensionContext));
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        return StoreSupport.getCatalog(extensionContext);
    }

}
