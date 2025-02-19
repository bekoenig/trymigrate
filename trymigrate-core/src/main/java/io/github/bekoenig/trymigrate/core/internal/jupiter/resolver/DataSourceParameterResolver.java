package io.github.bekoenig.trymigrate.core.internal.jupiter.resolver;

import io.github.bekoenig.trymigrate.core.internal.jupiter.StoreSupport;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolver;

import javax.sql.DataSource;
import java.util.Objects;

public class DataSourceParameterResolver implements ParameterResolver {

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        return parameterContext.getParameter().getType().equals(DataSource.class)
                && Objects.nonNull(StoreSupport.getDataSource(extensionContext));
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        return StoreSupport.getDataSource(extensionContext);
    }

}
