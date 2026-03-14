package io.github.bekoenig.trymigrate.core.internal.parameter;

import io.github.bekoenig.trymigrate.core.Trymigrate;
import io.github.bekoenig.trymigrate.core.internal.migrate.MigrateProcessor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;

import javax.sql.DataSource;
import java.lang.reflect.Parameter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class DataSourceParameterResolverTest {

    private final DataSourceParameterResolver resolver = new DataSourceParameterResolver();

    @Test
    @DisplayName("GIVEN a DataSource parameter WHEN checking support THEN return true if processor has dataSource")
    void shouldSupportDataSourceParameter() {
        // GIVEN
        ParameterContext parameterContext = mock();
        ExtensionContext extensionContext = mock();
        ExtensionContext.Store store = mock();
        MigrateProcessor processor = mock();
        DataSource dataSource = mock();
        Parameter parameter = mock();

        when(extensionContext.getStore(ExtensionContext.Namespace.create(Trymigrate.class))).thenReturn(store);
        when(store.get("migrate-processor", MigrateProcessor.class)).thenReturn(processor);
        when(processor.getDataSource()).thenReturn(dataSource);
        when(parameterContext.getParameter()).thenReturn(parameter);
        doReturn(DataSource.class).when(parameter).getType();

        // WHEN
        boolean result = resolver.supportsParameter(parameterContext, extensionContext);

        // THEN
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("GIVEN a DataSource parameter WHEN resolving THEN return dataSource from processor")
    void shouldResolveDataSourceParameter() {
        // GIVEN
        ParameterContext parameterContext = mock();
        ExtensionContext extensionContext = mock();
        ExtensionContext.Store store = mock();
        MigrateProcessor processor = mock();
        DataSource dataSource = mock();

        when(extensionContext.getStore(ExtensionContext.Namespace.create(Trymigrate.class))).thenReturn(store);
        when(store.get("migrate-processor", MigrateProcessor.class)).thenReturn(processor);
        when(processor.getDataSource()).thenReturn(dataSource);

        // WHEN
        Object result = resolver.resolveParameter(parameterContext, extensionContext);

        // THEN
        assertThat(result).isSameAs(dataSource);
    }

}
