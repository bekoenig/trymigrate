package io.github.bekoenig.trymigrate.core.internal;

import io.github.bekoenig.trymigrate.core.internal.lint.config.CoreLinters;
import io.github.bekoenig.trymigrate.core.internal.lint.report.html.LintsHtmlReporter;
import io.github.bekoenig.trymigrate.core.internal.lint.report.log.LintsLogReporter;
import io.github.bekoenig.trymigrate.core.lint.config.TrymigrateLintersCustomizer;
import io.github.bekoenig.trymigrate.core.lint.report.TrymigrateLintsReporter;
import io.github.bekoenig.trymigrate.core.plugin.TrymigrateBean;
import io.github.bekoenig.trymigrate.core.plugin.TrymigrateBeanProvider;
import io.github.bekoenig.trymigrate.core.plugin.TrymigratePlugin;
import io.github.bekoenig.trymigrate.core.plugin.customize.TrymigrateDataLoader;
import io.github.bekoenig.trymigrate.core.plugin.customize.TrymigrateFlywayCustomizer;
import org.flywaydb.core.api.callback.Callback;
import org.flywaydb.core.api.migration.JavaMigration;
import org.testcontainers.containers.JdbcDatabaseContainer;
import schemacrawler.schemacrawler.*;
import schemacrawler.tools.command.lint.options.LintOptions;
import schemacrawler.tools.command.lint.options.LintOptionsBuilder;
import us.fatehi.utility.database.SqlScript;

import java.sql.Connection;

import static io.github.bekoenig.trymigrate.core.plugin.customize.TrymigrateFlywayCustomizer.addCallbacks;

public class CorePlugin implements TrymigratePlugin {

    @TrymigrateBean
    private final LimitOptions limitOptions = LimitOptionsBuilder.newLimitOptions();

    @TrymigrateBean
    private final LoadOptions loadOptions = LoadOptionsBuilder.builder()
            .withSchemaInfoLevel(SchemaInfoLevelBuilder.maximum())
            .toOptions();

    @TrymigrateBean
    private TrymigrateFlywayCustomizer additionalBeanConfigurer;

    @TrymigrateBean
    private TrymigrateFlywayCustomizer containerDataSourceConfigurer;

    @TrymigrateBean
    private TrymigrateLintsReporter lintsLogReporter;

    @TrymigrateBean
    private TrymigrateLintsReporter lintsHtmlReporter;

    @TrymigrateBean
    private final TrymigrateLintersCustomizer lintersCustomizer = new CoreLinters();

    @TrymigrateBean
    private final TrymigrateDataLoader sqlDataLoadHandle = new TrymigrateDataLoader() {
        @Override
        public boolean supports(String resource, String extension) {
            return extension.equalsIgnoreCase("sql");
        }

        @Override
        public void load(String resource, Connection connection) {
            SqlScript.executeScriptFromResource(resource, connection);
        }
    };

    @Override
    public void populate(TrymigrateBeanProvider beanProvider) {
        this.additionalBeanConfigurer = configuration -> {
            addCallbacks(configuration, beanProvider.all(Callback.class));
            configuration.javaMigrations(beanProvider.all(JavaMigration.class).toArray(new JavaMigration[0]));
        };

        this.containerDataSourceConfigurer = configuration ->
                beanProvider.findOne(JdbcDatabaseContainer.class).ifPresent(c ->
                        configuration.dataSource(c.getJdbcUrl(), c.getUsername(), c.getPassword()));

        LintOptions lintOptions = beanProvider.findFirst(LintOptions.class)
                .orElseGet(() -> LintOptionsBuilder.builder()
                        .noInfo()
                        .toOptions());
        this.lintsLogReporter = new LintsLogReporter(lintOptions);
        this.lintsHtmlReporter = new LintsHtmlReporter(lintOptions);
    }
}
