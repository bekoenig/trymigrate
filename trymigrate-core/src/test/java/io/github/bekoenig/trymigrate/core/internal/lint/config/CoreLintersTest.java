package io.github.bekoenig.trymigrate.core.internal.lint.config;

import io.github.bekoenig.trymigrate.core.plugin.customize.TrymigrateLintersConfigurer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class CoreLintersTest {

    @Test
    @DisplayName("GIVEN CoreLinters WHEN accepted THEN many linters are configured")
    void shouldConfigureCoreLinters() {
        // GIVEN
        CoreLinters coreLinters = new CoreLinters();
        TrymigrateLintersConfigurer.TrymigrateLintersConfiguration config = mock(
                TrymigrateLintersConfigurer.TrymigrateLintersConfiguration.class);
        TrymigrateLintersConfigurer.TrymigrateLintersConfiguration.TrymigrateLinterConfiguration linterConfig = mock(
                TrymigrateLintersConfigurer.TrymigrateLintersConfiguration.TrymigrateLinterConfiguration.class);

        when(config.configure(anyString())).thenReturn(linterConfig);
        when(linterConfig.configure(anyString())).thenReturn(linterConfig);

        // WHEN
        coreLinters.accept(config);

        // THEN
        verify(config, atLeastOnce()).configure(anyString());
        verify(linterConfig, atLeast(10)).configure(anyString());
        verify(linterConfig).configure("schemacrawler.tools.linter.LinterTableWithNoPrimaryKey");
    }

}
