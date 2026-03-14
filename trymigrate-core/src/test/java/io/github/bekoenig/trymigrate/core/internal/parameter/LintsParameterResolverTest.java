package io.github.bekoenig.trymigrate.core.internal.parameter;

import io.github.bekoenig.trymigrate.core.Trymigrate;
import io.github.bekoenig.trymigrate.core.internal.migrate.MigrateProcessor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import schemacrawler.tools.lint.Lints;

import java.lang.reflect.Parameter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class LintsParameterResolverTest {

    private final LintsParameterResolver resolver = new LintsParameterResolver();

    @Test
    @DisplayName("GIVEN a Lints parameter WHEN checking support THEN return true if processor has lints")
    void shouldSupportLintsParameter() {
        // GIVEN
        ParameterContext parameterContext = mock();
        ExtensionContext extensionContext = mock();
        ExtensionContext.Store store = mock();
        MigrateProcessor processor = mock();
        Lints lints = mock();
        Parameter parameter = mock();

        when(extensionContext.getStore(ExtensionContext.Namespace.create(Trymigrate.class))).thenReturn(store);
        when(store.get("migrate-processor", MigrateProcessor.class)).thenReturn(processor);
        when(processor.getLints()).thenReturn(lints);
        when(parameterContext.getParameter()).thenReturn(parameter);
        doReturn(Lints.class).when(parameter).getType();

        // WHEN
        boolean result = resolver.supportsParameter(parameterContext, extensionContext);

        // THEN
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("GIVEN a Lints parameter WHEN resolving THEN return lints from processor")
    void shouldResolveLintsParameter() {
        // GIVEN
        ParameterContext parameterContext = mock();
        ExtensionContext extensionContext = mock();
        ExtensionContext.Store store = mock();
        MigrateProcessor processor = mock();
        Lints lints = mock();

        when(extensionContext.getStore(ExtensionContext.Namespace.create(Trymigrate.class))).thenReturn(store);
        when(store.get("migrate-processor", MigrateProcessor.class)).thenReturn(processor);
        when(processor.getLints()).thenReturn(lints);

        // WHEN
        Object result = resolver.resolveParameter(parameterContext, extensionContext);

        // THEN
        assertThat(result).isSameAs(lints);
    }

}
