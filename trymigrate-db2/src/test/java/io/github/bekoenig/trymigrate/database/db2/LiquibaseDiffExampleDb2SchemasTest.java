package io.github.bekoenig.trymigrate.database.db2;

import io.github.bekoenig.trymigrate.core.Trymigrate;
import io.github.bekoenig.trymigrate.core.TrymigrateWhenTarget;
import io.github.bekoenig.trymigrate.core.plugin.TrymigrateBean;
import io.github.bekoenig.trymigrate.core.plugin.customize.TrymigrateFlywayCustomizer;
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
import org.junit.jupiter.api.Test;
import org.testcontainers.db2.Db2Container;
import org.testcontainers.utility.DockerImageName;
import us.fatehi.utility.database.SqlScript;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@Trymigrate
public class LiquibaseDiffExampleDb2SchemasTest {

    protected static final String FLYWAY_SCHEMA = "EXAMPLE_SCHEMA";

    @TrymigrateBean
    private final TrymigrateFlywayCustomizer flywayCustomizer = configuration -> configuration
            .defaultSchema(LiquibaseDiffExampleDb2SchemasTest.FLYWAY_SCHEMA)
            .locations("classpath:db/migration/example/db2");

    @TrymigrateBean
    private final Db2Container db2Container =
            new Db2Container(DockerImageName.parse("icr.io/db2_community/db2:12.1.2.0"))
                    .acceptLicense();

    @Test
    @TrymigrateWhenTarget("latest")
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
        assertThat(diffResult.getUnexpectedObjects()).isEmpty();
        assertThat(diffResult.getMissingObjects()).isEmpty();
        assertThat(diffResult.getChangedObjects()).isEmpty();
    }

}
