package io.github.bekoenig.trymigrate.database.db2;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import io.github.bekoenig.assertj.schemacrawler.api.SchemaCrawlerAssertions;
import io.github.bekoenig.assertj.schemacrawler.api.TableAssert;
import io.github.bekoenig.trymigrate.core.Trymigrate;
import io.github.bekoenig.trymigrate.core.TrymigrateTest;
import io.github.bekoenig.trymigrate.core.plugin.TrymigrateBean;
import io.github.bekoenig.trymigrate.core.plugin.customize.TrymigrateDataLoader;
import io.github.bekoenig.trymigrate.core.internal.lint.report.log.LintsLogReporter;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.testcontainers.containers.Db2Container;
import org.testcontainers.utility.DockerImageName;
import schemacrawler.schema.Catalog;
import schemacrawler.tools.lint.LintSeverity;
import schemacrawler.tools.lint.Lints;

import java.nio.file.Path;
import java.sql.Connection;

import static java.util.function.Predicate.isEqual;
import static org.assertj.core.api.Assertions.assertThat;

@Trymigrate(
        flywayProperties = {
                "defaultSchema=EXAMPLE_SCHEMA",
                "locations=classpath:db/migration/example/db2",
                "cleanDisabled=false"
        }, failOn = LintSeverity.critical)
public class ExampleDb2SchemaTest {

    private ListAppender<ILoggingEvent> listAppender;

    private boolean trymigrateDataLoadHandleInvoked;

    @TrymigrateBean
    private final Db2Container db2Container =
            new Db2Container(DockerImageName.parse("icr.io/db2_community/db2:11.5.9.0"))
                    .acceptLicense();

    @TrymigrateBean
    private final TrymigrateDataLoader dataLoadHandle = new TrymigrateDataLoader() {

        @Override
        public boolean supports(String resource, String extension) {
            if (resource.equalsIgnoreCase("db/testdata/db2/initial.sql")) {
                trymigrateDataLoadHandleInvoked = true;
            }
            return false;
        }

        @Override
        public void load(String classpathResource, Connection connection) {
        }
    };

    @BeforeAll
    void beforeAll() {
        MDC.put("test-name", getClass().getName());
        Logger rootLogger = (Logger) LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);
        listAppender = (ListAppender<ILoggingEvent>) rootLogger.getAppender("LIST");
        listAppender.list.clear();
    }

    @AfterEach
    @AfterAll
    void afterTest() {
        listAppender.list.clear();
    }

    @TrymigrateTest(whenTarget = "1.0")
    void test_Initial(Catalog catalog, Lints lints) {
        assertThat(listAppender.list)
                .filteredOn(l -> l.getLoggerName().equals(LintsLogReporter.class.getName()))
                .filteredOn(l -> getClass().getName().equals(l.getMDCPropertyMap().get("test-name")))
                .singleElement()
                .extracting(ILoggingEvent::getMessage)
                .matches(m -> m.contains("no non-nullable data columns"))
                .matches(m -> m.contains("should have remarks\tENTITY1_ID, ATTRIBUTE1"));

        assertThat(lints).hasSize(6);

        assertThat(Path.of("target", "trymigrate-lint-reports", "EXAMPLE_SCHEMA", "1_0.html")).exists();

        assertThat(catalog).isNotNull();

        TableAssert tableAssert = SchemaCrawlerAssertions.assertThat(catalog)
                .table("EXAMPLE_SCHEMA", "EXAMPLE_ENTITY1");
        tableAssert.column("ENTITY1_ID")
                .matchesColumnDataTypeName(isEqual("CHAR")).matchesSize(isEqual(36));
        tableAssert.column("ATTRIBUTE1")
                .matchesColumnDataTypeName(isEqual("VARCHAR")).matchesSize(isEqual(250));
        tableAssert.tableConstraint("EXAMPLE_ENTITY1_PK")
                .constrainedColumn("ENTITY1_ID").isNotNull();
    }

    @TrymigrateTest(givenData = "db/testdata/db2/initial.sql", whenTarget = "1.1")
    @Order(1)
    void test_AddOptionalAttribute2_Schema(Catalog catalog,
                                           Lints lints) {
        assertThat(trymigrateDataLoadHandleInvoked).isTrue();

        assertThat(listAppender.list)
                .filteredOn(l -> l.getLoggerName().equals(LintsLogReporter.class.getName()))
                .filteredOn(l -> getClass().getName().equals(l.getMDCPropertyMap().get("test-name")))
                .singleElement()
                .extracting(ILoggingEvent::getMessage)
                .matches(m -> !m.contains("no non-nullable data columns"))
                .matches(m -> m.contains("should have remarks\tENTITY1_ID, ATTRIBUTE1, ATTRIBUTE2"));

        assertThat(lints).hasSize(8);

        assertThat(Path.of("target", "trymigrate-lint-reports", "EXAMPLE_SCHEMA", "1_1.html")).exists();

        SchemaCrawlerAssertions.assertThat(catalog)
                .column("EXAMPLE_SCHEMA", "EXAMPLE_ENTITY1", "ATTRIBUTE2")
                .matchesColumnDataTypeName(isEqual("INTEGER"))
                .isNullable(true);
    }

    @TrymigrateTest(whenTarget = "1.1")
    @Order(2)
    void test_AddOptionalAttribute2_Data(Lints lints) {
        assertThat(listAppender.list)
                .filteredOn(l -> l.getLoggerName().equals(LintsLogReporter.class.getName()))
                .filteredOn(l -> getClass().getName().equals(l.getMDCPropertyMap().get("test-name")))
                .isEmpty();

        assertThat(lints).hasSize(8);
    }

    @TrymigrateTest(
            cleanBefore = true,
            givenData = {
                    "INSERT INTO EXAMPLE_SCHEMA.EXAMPLE_ENTITY1 (ENTITY1_ID, ATTRIBUTE1, ATTRIBUTE2) VALUES ('3019e6cc-386a-4c15-af62-60bddb438faf', 'v1.1-value3', 4711);"
            },
            whenTarget = "1.3"
    )
    void test_EnforceAttribute2(Catalog catalog,
                                Lints lints) {
        assertThat(listAppender.list)
                .filteredOn(l -> l.getLoggerName().equals(LintsLogReporter.class.getName()))
                .filteredOn(l -> getClass().getName().equals(l.getMDCPropertyMap().get("test-name")))
                .hasSize(2);

        assertThat(lints).hasSize(7);

        assertThat(Path.of("target", "trymigrate-lint-reports", "EXAMPLE_SCHEMA", "1_3.html")).doesNotExist();

        SchemaCrawlerAssertions.assertThat(catalog)
                .column("EXAMPLE_SCHEMA", "EXAMPLE_ENTITY1", "ATTRIBUTE2")
                .isNullable(false);
    }

}
