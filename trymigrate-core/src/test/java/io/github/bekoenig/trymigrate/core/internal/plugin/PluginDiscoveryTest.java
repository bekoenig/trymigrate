package io.github.bekoenig.trymigrate.core.internal.plugin;

import io.github.bekoenig.trymigrate.core.plugin.TrymigratePlugin;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PluginDiscoveryTest {

    interface P1 extends TrymigratePlugin {}
    interface P2 extends P1 {}
    interface P3 extends P2 {}
    interface O1 extends P1 {}

    static class P0Impl implements TrymigratePlugin {}
    static class P1Impl implements P1 {}
    static class P2Impl implements P2 {}
    static class P3Impl implements P3 {}
    static class O1Impl implements O1 {}

    @Test
    void hasCommonSuperinterface() {
        assertThat(PluginDiscovery.hasCommonSuperinterface(P0Impl.class, P1.class)).isTrue();
        assertThat(PluginDiscovery.hasCommonSuperinterface(P1Impl.class, P2.class)).isTrue();
        assertThat(PluginDiscovery.hasCommonSuperinterface(P2Impl.class, P3.class)).isTrue();
        assertThat(PluginDiscovery.hasCommonSuperinterface(P1Impl.class, P3.class)).isTrue();
        assertThat(PluginDiscovery.hasCommonSuperinterface(P1Impl.class, O1.class)).isTrue();
        assertThat(PluginDiscovery.hasCommonSuperinterface(P2Impl.class, O1.class)).isFalse();

        assertThat(PluginDiscovery.hasCommonSuperinterface(P1Impl.class, TrymigratePlugin.class)).isTrue();
        assertThat(PluginDiscovery.hasCommonSuperinterface(P2Impl.class, TrymigratePlugin.class)).isTrue();
        assertThat(PluginDiscovery.hasCommonSuperinterface(P3Impl.class, TrymigratePlugin.class)).isTrue();
        assertThat(PluginDiscovery.hasCommonSuperinterface(O1Impl.class, TrymigratePlugin.class)).isTrue();
        assertThat(PluginDiscovery.hasCommonSuperinterface(P3Impl.class, P2.class)).isTrue();
        assertThat(PluginDiscovery.hasCommonSuperinterface(O1Impl.class, P1.class)).isTrue();
        assertThat(PluginDiscovery.hasCommonSuperinterface(O1Impl.class, P2.class)).isFalse();
    }

    @SuppressWarnings("unchecked")
    @Test
    void ofType() {
        assertThat(PluginDiscovery.ofType(P0Impl.class, new Class[]{ P0Impl.class })).isTrue();
        assertThat(PluginDiscovery.ofType(P0Impl.class, new Class[]{ TrymigratePlugin.class })).isTrue();
        assertThat(PluginDiscovery.ofType(P0Impl.class, new Class[]{ TrymigratePlugin.class, P0Impl.class })).isTrue();

        assertThat(PluginDiscovery.ofType(P1Impl.class, new Class[]{ P0Impl.class, P1Impl.class })).isTrue();
        assertThat(PluginDiscovery.ofType(P1Impl.class, new Class[]{ P1.class })).isTrue();
        assertThat(PluginDiscovery.ofType(P1Impl.class, new Class[]{ P0Impl.class })).isFalse();

        assertThat(PluginDiscovery.ofType(P3Impl.class, new Class[]{ P2.class })).isTrue();
        assertThat(PluginDiscovery.ofType(P3Impl.class, new Class[]{ P2Impl.class })).isFalse();

        assertThat(PluginDiscovery.ofType(O1Impl.class, new Class[]{ P1.class })).isTrue();
        assertThat(PluginDiscovery.ofType(O1Impl.class, new Class[]{ P1Impl.class })).isFalse();
        assertThat(PluginDiscovery.ofType(P1Impl.class, new Class[]{ O1.class })).isFalse();
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