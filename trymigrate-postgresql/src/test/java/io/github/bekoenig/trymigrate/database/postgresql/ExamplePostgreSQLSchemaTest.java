package io.github.bekoenig.trymigrate.database.postgresql;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import io.github.bekoenig.assertj.schemacrawler.api.SchemaCrawlerAssertions;
import io.github.bekoenig.assertj.schemacrawler.api.TableAssert;
import io.github.bekoenig.trymigrate.core.Trymigrate;
import io.github.bekoenig.trymigrate.core.plugin.TrymigrateBean;
import io.github.bekoenig.trymigrate.core.TrymigrateTest;
import io.github.bekoenig.trymigrate.core.internal.lint.report.log.LintsLogReporter;
import io.github.bekoenig.trymigrate.core.lint.TrymigrateSuppressLint;
import io.github.bekoenig.trymigrate.core.lint.TrymigrateExcludeLint;
import org.flywaydb.core.api.callback.Callback;
import org.flywaydb.core.api.callback.Context;
import org.flywaydb.core.api.callback.Event;
import org.flywaydb.core.api.migration.JavaMigration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;
import schemacrawler.schema.Catalog;
import schemacrawler.tools.lint.LintSeverity;
import schemacrawler.tools.lint.Lints;

import javax.sql.DataSource;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.List;

import static java.util.function.Predicate.isEqual;
import static org.assertj.core.api.Assertions.assertThat;

@Trymigrate(
        flywayProperties = {
                "defaultSchema=example_schema",
                "locations=classpath:db/migration/example/postgresql",
                "cleanDisabled=false"
//                "url=jdbc:postgresql://localhost:5432/test",
//                "user=test",
//                "password=test",
        }, failOn = LintSeverity.medium)
@TrymigrateExcludeLint(linterId = "schemacrawler.tools.linter.LinterTableSql")
@TrymigrateExcludeLint(linterId = "schemacrawler.tools.linter.LinterTableEmpty")
public class ExamplePostgreSQLSchemaTest {

    @TrymigrateBean
    private final PostgreSQLContainer<?> containerDatabase = new PostgreSQLContainer<>(
            DockerImageName.parse("postgres:17.2"));

    @TrymigrateBean
    private final List<JavaMigration> javaMigrations = List.of(new NoopJavaMigration("1.0.1"));

    @TrymigrateBean
    private final NoopJavaMigration moreNoop = new NoopJavaMigration("1.0.2");

    @TrymigrateBean(nullable = true)
    private Callback hookedCallback;

    private ListAppender<ILoggingEvent> listAppender;

    @BeforeAll
    void beforeAll() {
        MDC.put("test-name", getClass().getName());
        Logger rootLogger = (Logger) LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);
        listAppender = (ListAppender<ILoggingEvent>) rootLogger.getAppender("LIST");
    }

    @AfterEach
    @AfterAll
    void afterTest() {
        listAppender.list.clear();
    }

    @TrymigrateTest(whenTarget = "1.0", cleanBefore = true)
    @TrymigrateSuppressLint(
            linterId = "schemacrawler.tools.linter.LinterTableAllNullableColumns",
            objectName = "example_schema.example_entity1")
    @TrymigrateSuppressLint(
            linterId = "io.github.bekoenig.trymigrate.database.postgresql.DummyLinter",
            objectName = "example_schema.example_entity1"
    )
    @TrymigrateSuppressLint(
            linterId = "schemacrawler.tools.linter.LinterTableWithNoRemarks",
            objectName = "example_schema.example_entity1.*"
    )
    void test_Initial(Catalog catalog,
                      DataSource dataSource,
                      Lints lints) throws URISyntaxException {
        assertThat(listAppender.list)
                .filteredOn(l -> l.getLoggerName().equals(LintsLogReporter.class.getName()))
                .filteredOn(l -> getClass().getName().equals(l.getMDCPropertyMap().get("test-name")))
                .singleElement()
                .extracting(ILoggingEvent::getMessage)
                .matches(m -> m.contains("no non-nullable data columns"))
                .matches(m -> m.contains("should have remarks\tentity1_id, attribute1"));

        assertThat(Path.of(ClassLoader.getSystemResource("./").toURI())
                .getParent()
                .resolve("trymigrate-lint-reports")
                .resolve("example_schema")
                .resolve("1_0.html")).exists();

        assertThat(catalog).isNotNull();
        assertThat(dataSource).isNotNull();

        assertThat(lints).hasSize(7);

        assertThat(catalog.getSchemas()).hasSize(1);

        TableAssert tableAssert = SchemaCrawlerAssertions.assertThat(catalog)
                .table("example_schema", "example_entity1");
        tableAssert.column("entity1_id")
                .matchesColumnDataTypeName(isEqual("bpchar")).matchesSize(isEqual(36));
        tableAssert.column("attribute1")
                .matchesColumnDataTypeName(isEqual("varchar")).matchesSize(isEqual(250));
        tableAssert.tableConstraint("example_entity1_pk")
                .constrainedColumn("entity1_id").isNotNull();

        hookedCallback = new Callback() {
            @Override
            public boolean supports(Event event, Context context) {
                return true;
            }

            @Override
            public boolean canHandleInTransaction(Event event, Context context) {
                return true;
            }

            @Override
            public void handle(Event event, Context context) {
                System.out.println(event);
            }

            @Override
            public String getCallbackName() {
                return "NoName";
            }
        };
    }

    @TrymigrateTest(whenTarget = "1.1")
    public void test_Comments(Lints lints) {
        assertThat(lints).hasSize(5);
    }

    @Test
    public void test_migrateToLatest() {
    }

    @Test
    public void test_noMigrate() {
    }

}
