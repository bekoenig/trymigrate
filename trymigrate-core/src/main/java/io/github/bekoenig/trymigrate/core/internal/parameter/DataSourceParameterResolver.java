package io.github.bekoenig.trymigrate.core.internal.parameter;

import io.github.bekoenig.trymigrate.core.internal.StoreSupport;
import org.junit.jupiter.api.extension.ExtensionContext;

import javax.sql.DataSource;

public class DataSourceParameterResolver extends AbstractParameterResolver<DataSource> {

    @Override
    protected Class<DataSource> forType() {
        return DataSource.class;
    }

    @Override
    protected DataSource currentValue(ExtensionContext extensionContext) {
        return StoreSupport.getMigrateProcessor(extensionContext).getDataSource();
    }

}
