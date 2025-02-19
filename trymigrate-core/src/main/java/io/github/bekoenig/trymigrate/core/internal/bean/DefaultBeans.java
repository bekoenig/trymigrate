package io.github.bekoenig.trymigrate.core.internal.bean;

import io.github.bekoenig.trymigrate.core.config.TrymigrateBean;
import io.github.bekoenig.trymigrate.core.config.TrymigrateBeanProvider;
import io.github.bekoenig.trymigrate.core.config.TrymigrateFlywayConfigurer;
import io.github.bekoenig.trymigrate.core.internal.schemacrawler.lint.config.DefaultLinters;
import io.github.bekoenig.trymigrate.core.internal.schemacrawler.lint.report.DefaultLintsReportResolver;
import io.github.bekoenig.trymigrate.core.internal.schemacrawler.lint.report.LintsHtmlReporter;
import io.github.bekoenig.trymigrate.core.internal.schemacrawler.lint.report.LintsLogReporter;
import io.github.bekoenig.trymigrate.core.lint.LintersCustomizer;
import io.github.bekoenig.trymigrate.core.lint.LintsReporter;
import io.github.bekoenig.trymigrate.core.lint.LintsReportResolver;
import org.flywaydb.core.api.callback.Callback;
import org.flywaydb.core.api.migration.JavaMigration;
import org.testcontainers.containers.JdbcDatabaseContainer;

import static io.github.bekoenig.trymigrate.core.config.TrymigrateFlywayConfigurer.addCallbacks;

public class DefaultBeans {

    @TrymigrateBean
    private final TrymigrateFlywayConfigurer additionalBeanConfigurer;

    @TrymigrateBean
    private final TrymigrateFlywayConfigurer containerDataSourceConfigurer;

    @TrymigrateBean
    private final LintsReporter lintsLogReporter = new LintsLogReporter();

    @TrymigrateBean
    private final LintsReporter lintsHtmlReporter;

    @TrymigrateBean
    private final LintersCustomizer lintersCustomizer = linterConfiguration -> linterConfiguration.merge(new DefaultLinters());

    public DefaultBeans(TrymigrateBeanProvider beanProvider) {
        this.additionalBeanConfigurer = configuration -> {
            addCallbacks(configuration, beanProvider.all(Callback.class));
            configuration.javaMigrations(beanProvider.all(JavaMigration.class).toArray(new JavaMigration[0]));
        };

        this.containerDataSourceConfigurer = configuration -> beanProvider.findOne(JdbcDatabaseContainer.class)
                .ifPresent(c -> configuration.dataSource(c.getJdbcUrl(), c.getUsername(), c.getPassword()));

        this.lintsHtmlReporter = new LintsHtmlReporter(
                beanProvider.findFirst(LintsReportResolver.class).orElseGet(DefaultLintsReportResolver::new));
    }


}
