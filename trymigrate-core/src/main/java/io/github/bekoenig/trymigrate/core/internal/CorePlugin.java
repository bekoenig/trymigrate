package io.github.bekoenig.trymigrate.core.internal;

import io.github.bekoenig.trymigrate.core.internal.data.SqlDataLoader;
import io.github.bekoenig.trymigrate.core.internal.lint.config.CoreLinters;
import io.github.bekoenig.trymigrate.core.internal.lint.report.LintsHtmlReporter;
import io.github.bekoenig.trymigrate.core.internal.lint.report.LintsLogReporter;
import io.github.bekoenig.trymigrate.core.lint.report.TrymigrateLintsReporter;
import io.github.bekoenig.trymigrate.core.plugin.TrymigrateBean;
import io.github.bekoenig.trymigrate.core.plugin.TrymigratePlugin;
import io.github.bekoenig.trymigrate.core.plugin.TrymigratePluginProvider;
import io.github.bekoenig.trymigrate.core.plugin.customize.TrymigrateDataLoader;

import java.util.List;

public class CorePlugin implements TrymigratePlugin {

    public static class CorePluginProvider implements TrymigratePluginProvider<CorePlugin> {
        @Override
        public CorePlugin provide() {
            return new CorePlugin();
        }
    }

    @TrymigrateBean
    private final List<TrymigrateLintsReporter> lintsLogReporter = List.of(
            new LintsLogReporter(), new LintsHtmlReporter());

    @TrymigrateBean
    private final CoreLinters coreLinters = new CoreLinters();

    @TrymigrateBean
    private final TrymigrateDataLoader sqlDataLoadHandle = new SqlDataLoader();

}