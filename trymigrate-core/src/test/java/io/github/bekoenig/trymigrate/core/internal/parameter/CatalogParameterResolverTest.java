package io.github.bekoenig.trymigrate.core.internal.parameter;

import io.github.bekoenig.trymigrate.core.Trymigrate;
import io.github.bekoenig.trymigrate.core.internal.migrate.MigrateProcessor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import schemacrawler.schema.Catalog;

import java.lang.reflect.Parameter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class CatalogParameterResolverTest {

    private final CatalogParameterResolver resolver = new CatalogParameterResolver();

    @Test
    @DisplayName("GIVEN a Catalog parameter WHEN checking support THEN return true if processor has catalog")
    void shouldSupportCatalogParameter() {
        // GIVEN
        ParameterContext parameterContext = mock();
        ExtensionContext extensionContext = mock();
        ExtensionContext.Store store = mock();
        MigrateProcessor processor = mock();
        Catalog catalog = mock();
        Parameter parameter = mock();

        when(extensionContext.getStore(ExtensionContext.Namespace.create(Trymigrate.class))).thenReturn(store);
        when(store.get("migrate-processor", MigrateProcessor.class)).thenReturn(processor);
        when(processor.getCatalog()).thenReturn(catalog);
        when(parameterContext.getParameter()).thenReturn(parameter);
        doReturn(Catalog.class).when(parameter).getType();

        // WHEN
        boolean result = resolver.supportsParameter(parameterContext, extensionContext);

        // THEN
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("GIVEN a Catalog parameter WHEN resolving THEN return catalog from processor")
    void shouldResolveCatalogParameter() {
        // GIVEN
        ParameterContext parameterContext = mock();
        ExtensionContext extensionContext = mock();
        ExtensionContext.Store store = mock();
        MigrateProcessor processor = mock();
        Catalog catalog = mock();

        when(extensionContext.getStore(ExtensionContext.Namespace.create(Trymigrate.class))).thenReturn(store);
        when(store.get("migrate-processor", MigrateProcessor.class)).thenReturn(processor);
        when(processor.getCatalog()).thenReturn(catalog);

        // WHEN
        Object result = resolver.resolveParameter(parameterContext, extensionContext);

        // THEN
        assertThat(result).isSameAs(catalog);
    }

}
