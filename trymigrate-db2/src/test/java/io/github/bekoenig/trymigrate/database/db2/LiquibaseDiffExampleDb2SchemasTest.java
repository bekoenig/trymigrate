package io.github.bekoenig.trymigrate.database.db2;

import io.github.bekoenig.trymigrate.core.Trymigrate;
import io.github.bekoenig.trymigrate.core.TrymigrateTest;
import io.github.bekoenig.trymigrate.core.plugin.TrymigrateBean;
import io.github.bekoenig.trymigrate.core.lint.IgnoreLint;
import liquibase.GlobalConfiguration;
import liquibase.Scope;
import liquibase.command.CommandResults;
import liquibase.command.CommandScope;
import liquibase.command.core.DiffChangelogCommandStep;
import liquibase.command.core.DiffCommandStep;
import liquibase.command.core.helpers.DbUrlConnectionArgumentsCommandStep;
import liquibase.command.core.helpers.PreCompareCommandStep;
import liquibase.command.core.helpers.ReferenceDbUrlConnectionCommandStep;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.diff.DiffResult;
import org.testcontainers.containers.Db2Container;
import org.testcontainers.utility.DockerImageName;
import us.fatehi.utility.database.SqlScript;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Beispiel zur Verwendung von Liquibase zum Abgleich des Flyway-Schemas gegen einen Schema-Dump.
 */
@Trymigrate(
        flywayProperties = {
                "defaultSchema=" + LiquibaseDiffExampleDb2SchemasTest.FLYWAY_SCHEMA,
                "locations=classpath:db/migration/example/db2"
        }
)
@IgnoreLint(linterId = ".*", objectName = ".*")
public class LiquibaseDiffExampleDb2SchemasTest {

    protected static final String FLYWAY_SCHEMA = "EXAMPLE_SCHEMA";

    @TrymigrateBean
    private final Db2Container db2Container =
            new Db2Container(DockerImageName.parse("icr.io/db2_community/db2:11.5.9.0"))
                    .acceptLicense();

    @TrymigrateTest(whenTarget = "latest")
        // Der Tests wird nach Anwendung aller Migrationen durchgeführt.
    void test_latest(DataSource dataSource) throws Exception {
        // GIVEN
        // WHEN
        DiffResult diffResult;
        try (Connection referenceConnection = dataSource.getConnection();
             JdbcConnection referenceJdbcConnection = new JdbcConnection(referenceConnection);
             Connection connection = dataSource.getConnection();
             JdbcConnection jdbcConnection = new JdbcConnection(connection)) {

            referenceConnection.setSchema(FLYWAY_SCHEMA + "_DUMP");
            SqlScript.executeScriptFromResource("example_schema_1_4.sql", referenceConnection);
            Database referenceDatabase = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(referenceJdbcConnection);

            connection.setSchema(FLYWAY_SCHEMA);
            Database targetDatabase = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(
                    jdbcConnection);

            CommandResults execute = Scope.child(
                    // Beispiel: Überschreiben von globalen Properties für den lokalen Scope.
                    Map.of(GlobalConfiguration.DIFF_COLUMN_ORDER.getKey(), true),
                    () -> new CommandScope(DiffChangelogCommandStep.COMMAND_NAME)
                            .addArgumentValue(ReferenceDbUrlConnectionCommandStep.REFERENCE_DATABASE_ARG, referenceDatabase)
                            .addArgumentValue(DbUrlConnectionArgumentsCommandStep.DATABASE_ARG, targetDatabase)
                            .addArgumentValue(DiffChangelogCommandStep.CHANGELOG_FILE_ARG, " ")
                            .addArgumentValue(PreCompareCommandStep.EXCLUDE_OBJECTS_ARG, "flyway_schema_history")
                            .addArgumentValue(PreCompareCommandStep.DIFF_TYPES_ARG,
                                    "catalog,tables,views,columns,indexes,foreignkeys,primarykeys,sequence,uniqueconstraints")
                            .execute());

            diffResult = execute.getResult(DiffCommandStep.DIFF_RESULT);
        }

        // THEN
        assertThat(diffResult.getUnexpectedObjects()).as("Keine unerwarteten Objekte").isEmpty();
        assertThat(diffResult.getMissingObjects()).as("Keine fehlenden Objekte").isEmpty();
        assertThat(diffResult.getChangedObjects()).as("Keine abweichenden Objekte").isEmpty();
    }

}
