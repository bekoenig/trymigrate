package io.github.bekoenig.trymigrate.core.internal;

import io.github.bekoenig.trymigrate.core.plugin.*;
import io.github.bekoenig.trymigrate.core.plugin.customize.TrymigrateDataLoader;
import io.github.bekoenig.trymigrate.core.plugin.customize.TrymigrateFlywayConfigurer;
import io.github.bekoenig.trymigrate.core.internal.schemacrawler.lint.config.DefaultLinters;
import io.github.bekoenig.trymigrate.core.internal.schemacrawler.lint.report.DefaultLintsReportResolver;
import io.github.bekoenig.trymigrate.core.internal.schemacrawler.lint.report.LintsHtmlReporter;
import io.github.bekoenig.trymigrate.core.internal.schemacrawler.lint.report.LintsLogReporter;
import io.github.bekoenig.trymigrate.core.internal.testcontainers.StartContainer;
import io.github.bekoenig.trymigrate.core.internal.testcontainers.StaticPortBinding;
import io.github.bekoenig.trymigrate.core.lint.config.LintersCustomizer;
import io.github.bekoenig.trymigrate.core.lint.report.LintsReportResolver;
import io.github.bekoenig.trymigrate.core.lint.report.LintsReporter;
import org.flywaydb.core.api.callback.Callback;
import org.flywaydb.core.api.migration.JavaMigration;
import org.junit.jupiter.api.Order;
import org.testcontainers.containers.JdbcDatabaseContainer;
import schemacrawler.schemacrawler.*;
import us.fatehi.utility.database.SqlScript;

import java.sql.Connection;

import static io.github.bekoenig.trymigrate.core.plugin.customize.TrymigrateFlywayConfigurer.addCallbacks;

public class CorePlugin implements TrymigratePlugin {

    @TrymigrateBean
    private final LimitOptions limitOptions = LimitOptionsBuilder.newLimitOptions();

    @TrymigrateBean
    private final LoadOptions loadOptions = LoadOptionsBuilder.builder()
            .withSchemaInfoLevel(SchemaInfoLevelBuilder.maximum())
            .toOptions();

    @TrymigrateBean
    private TrymigrateFlywayConfigurer additionalBeanConfigurer;

    @TrymigrateBean
    private TrymigrateFlywayConfigurer containerDataSourceConfigurer;

    @TrymigrateBean
    private final LintsReporter lintsLogReporter = new LintsLogReporter();

    @TrymigrateBean
    private LintsReporter lintsHtmlReporter;

    @TrymigrateBean
    private final LintersCustomizer lintersCustomizer = new DefaultLinters();

    @TrymigrateBean
    private final TrymigrateDataLoader sqlDataLoadHandle = new TrymigrateDataLoader() {
        @Override
        public boolean supports(String resource, String extension) {
            return extension.equalsIgnoreCase("sql");
        }

        @Override
        public void handle(String resource, Connection connection) {
            SqlScript.executeScriptFromResource(resource, connection);
        }
    };

    @TrymigrateBean
    private final StaticPortBinding staticPortBinding = new StaticPortBinding();

    @Order(Order.DEFAULT + 1)
    @TrymigrateBean
    private final StartContainer startContainer = new StartContainer();

    @Override
    public void populate(TrymigrateBeanProvider beanProvider) {
        this.additionalBeanConfigurer = configuration -> {
            addCallbacks(configuration, beanProvider.all(Callback.class));
            configuration.javaMigrations(beanProvider.all(JavaMigration.class).toArray(new JavaMigration[0]));
        };

        this.containerDataSourceConfigurer = configuration ->
                beanProvider.findOne(JdbcDatabaseContainer.class).ifPresent(c ->
                        configuration.dataSource(c.getJdbcUrl(), c.getUsername(), c.getPassword()));

        this.lintsHtmlReporter = new LintsHtmlReporter(
                beanProvider.findFirst(LintsReportResolver.class).orElseGet(DefaultLintsReportResolver::new));
    }
}
