package io.github.bekoenig.trymigrate.core.internal.bean;

import io.github.bekoenig.trymigrate.core.config.TrymigratePlugin;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PluginDiscoveryTest {

    interface P1 extends TrymigratePlugin {}
    interface P2 extends P1 {}
    interface P3 extends P2 {}
    interface O1 extends P1 {}

    @Test
    void hasCompatibleSuperinterface() {
        assertThat(PluginDiscovery.hasCompatibleSuperinterface(TrymigratePlugin.class, P1.class)).isTrue();
        assertThat(PluginDiscovery.hasCompatibleSuperinterface(P1.class, P2.class)).isTrue();
        assertThat(PluginDiscovery.hasCompatibleSuperinterface(P2.class, P3.class)).isTrue();
        assertThat(PluginDiscovery.hasCompatibleSuperinterface(P1.class, P3.class)).isTrue();
        assertThat(PluginDiscovery.hasCompatibleSuperinterface(P1.class, O1.class)).isTrue();
        assertThat(PluginDiscovery.hasCompatibleSuperinterface(P2.class, O1.class)).isFalse();

        assertThat(PluginDiscovery.hasCompatibleSuperinterface(P1.class, TrymigratePlugin.class)).isTrue();
        assertThat(PluginDiscovery.hasCompatibleSuperinterface(P2.class, TrymigratePlugin.class)).isTrue();
        assertThat(PluginDiscovery.hasCompatibleSuperinterface(P3.class, TrymigratePlugin.class)).isTrue();
        assertThat(PluginDiscovery.hasCompatibleSuperinterface(O1.class, TrymigratePlugin.class)).isTrue();
        assertThat(PluginDiscovery.hasCompatibleSuperinterface(P3.class, P2.class)).isTrue();
        assertThat(PluginDiscovery.hasCompatibleSuperinterface(O1.class, P1.class)).isTrue();
        assertThat(PluginDiscovery.hasCompatibleSuperinterface(O1.class, P2.class)).isFalse();
    }

    @Test
    void countIntermediateInterfaces() {
        assertThat(PluginDiscovery.countIntermediateInterfaces(TrymigratePlugin.class)).isEqualTo(0);
        assertThat(PluginDiscovery.countIntermediateInterfaces(P1.class)).isEqualTo(1);
        assertThat(PluginDiscovery.countIntermediateInterfaces(P2.class)).isEqualTo(2);
        assertThat(PluginDiscovery.countIntermediateInterfaces(P3.class)).isEqualTo(3);

        class P0Impl implements TrymigratePlugin {}
        assertThat(PluginDiscovery.countIntermediateInterfaces(P0Impl.class)).isEqualTo(0);

        interface P12 extends P1, P2 {}
        assertThat(PluginDiscovery.countIntermediateInterfaces(P12.class)).isEqualTo(3);

        interface P02 extends TrymigratePlugin, P2 {}
        assertThat(PluginDiscovery.countIntermediateInterfaces(P02.class)).isEqualTo(3);
    }
}