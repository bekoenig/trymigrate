package io.github.bekoenig.trymigrate.core.internal.parameter;

import io.github.bekoenig.trymigrate.core.internal.StoreSupport;
import org.junit.jupiter.api.extension.ExtensionContext;
import schemacrawler.schema.Catalog;

public class CatalogParameterResolver extends AbstractParameterResolver<Catalog> {

    @Override
    protected Class<Catalog> forType() {
        return Catalog.class;
    }

    @Override
    protected Catalog currentValue(ExtensionContext extensionContext) {
        return StoreSupport.getMigrateProcessor(extensionContext).getCatalog();
    }

}
