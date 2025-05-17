package io.github.bekoenig.trymigrate.core.internal.plugin;

import io.github.bekoenig.trymigrate.core.plugin.TrymigratePlugin;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PluginDiscoveryTest {

    interface P1 extends TrymigratePlugin {}
    interface P2 extends P1 {}
    interface P3 extends P2 {}
    interface O1 extends P1 {}

    @Test
    void hasCompatibleSuperinterface() {
        class P0Impl implements TrymigratePlugin {}
        class P1Impl implements P1 {}
        class P2Impl implements P2 {}
        class P3Impl implements P3 {}
        class O1Impl implements O1 {}

        assertThat(PluginDiscovery.hasCompatibleSuperinterface(P0Impl.class, P1.class)).isTrue();
        assertThat(PluginDiscovery.hasCompatibleSuperinterface(P1Impl.class, P2.class)).isTrue();
        assertThat(PluginDiscovery.hasCompatibleSuperinterface(P2Impl.class, P3.class)).isTrue();
        assertThat(PluginDiscovery.hasCompatibleSuperinterface(P1Impl.class, P3.class)).isTrue();
        assertThat(PluginDiscovery.hasCompatibleSuperinterface(P1Impl.class, O1.class)).isTrue();
        assertThat(PluginDiscovery.hasCompatibleSuperinterface(P2Impl.class, O1.class)).isFalse();

        assertThat(PluginDiscovery.hasCompatibleSuperinterface(P1Impl.class, TrymigratePlugin.class)).isTrue();
        assertThat(PluginDiscovery.hasCompatibleSuperinterface(P2Impl.class, TrymigratePlugin.class)).isTrue();
        assertThat(PluginDiscovery.hasCompatibleSuperinterface(P3Impl.class, TrymigratePlugin.class)).isTrue();
        assertThat(PluginDiscovery.hasCompatibleSuperinterface(O1Impl.class, TrymigratePlugin.class)).isTrue();
        assertThat(PluginDiscovery.hasCompatibleSuperinterface(P3Impl.class, P2.class)).isTrue();
        assertThat(PluginDiscovery.hasCompatibleSuperinterface(O1Impl.class, P1.class)).isTrue();
        assertThat(PluginDiscovery.hasCompatibleSuperinterface(O1Impl.class, P2.class)).isFalse();
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