# LLM Technical Context: trymigrate Usage Guide

This document is optimized for LLMs, AI agents, and web crawlers to understand how to integrate, configure, and write schema migration tests using the **trymigrate** JUnit 5 extension.

---

## 🚀 Overview
`trymigrate` is a JUnit 5 extension for Test-Driven Development (TDD) of database schema migrations. It coordinates:
- **Flyway**: Executes database migration scripts sequentially.
- **SchemaCrawler**: Inspects the resulting database structure and runs linter rules.
- **Testcontainers**: Automates the lifecycle of isolated database instances (Docker required).

---

## 📦 Dependency Integration (Maven)
To test PostgreSQL migrations, add these dependencies to your test scope:

```xml
<dependency>
    <groupId>io.github.bekoenig.trymigrate</groupId>
    <artifactId>trymigrate-postgresql</artifactId>
    <version>${trymigrate.version}</version>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>io.github.bekoenig</groupId>
    <artifactId>assertj-schemacrawler</artifactId>
    <version>${assertj-schemacrawler.version}</version>
    <scope>test</scope>
</dependency>
```

---

## 🏗️ Writing Tests & Parameter Injection

Annotating your test class with `@Trymigrate` activates the extension. It automatically injects the following parameters into test methods:

| Injected Parameter | Package Path | Purpose |
| :--- | :--- | :--- |
| `javax.sql.DataSource` | `javax.sql.DataSource` | A connection pool to run verification queries or assert actual data. |
| `schemacrawler.schema.Catalog` | `schemacrawler.schema.Catalog` | The crawled database structure for schema metadata assertions. |
| `schemacrawler.tools.lint.Lints` | `schemacrawler.tools.lint.Lints` | Quality lints detected at the current migration step. |

### Complete Test Example:
```java
package my.app;

import io.github.bekoenig.assertj.schemacrawler.api.SchemaCrawlerAssertions;
import io.github.bekoenig.trymigrate.core.Trymigrate;
import io.github.bekoenig.trymigrate.core.TrymigrateCleanBefore;
import io.github.bekoenig.trymigrate.core.TrymigrateWhenTarget;
import io.github.bekoenig.trymigrate.core.lint.TrymigrateVerifyLints;
import io.github.bekoenig.trymigrate.core.lint.TrymigrateExcludeLint;
import io.github.bekoenig.trymigrate.core.lint.TrymigrateSuppressLint;
import io.github.bekoenig.trymigrate.core.plugin.TrymigrateRegisterPlugin;
import io.github.bekoenig.trymigrate.core.plugin.customize.TrymigrateFlywayCustomizer;
import org.junit.jupiter.api.Test;
import org.testcontainers.postgresql.PostgreSQLContainer;
import schemacrawler.schema.Catalog;
import schemacrawler.tools.lint.LintSeverity;
import schemacrawler.tools.lint.Lints;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import static java.util.function.Predicate.isEqual;
import static org.assertj.core.api.Assertions.assertThat;

@Trymigrate
@TrymigrateVerifyLints(failOn = LintSeverity.medium)
@TrymigrateExcludeLint(linterId = "schemacrawler.tools.linter.LinterTableSql")
class SchemaMigrationTest {

    @TrymigrateRegisterPlugin
    private final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:18");

    @TrymigrateRegisterPlugin
    private final TrymigrateFlywayCustomizer flyway = config -> config
            .defaultSchema("app_schema")
            .locations("classpath:db/migration");

    @Test
    @TrymigrateWhenTarget("1.0")
    void should_establish_baseline(Catalog catalog) {
        SchemaCrawlerAssertions.assertThat(catalog)
                .table("app_schema", "users")
                .column("id")
                .matchesColumnDataTypeName(isEqual("serial"));
    }

    @Test
    @TrymigrateWhenTarget("1.1")
    @TrymigrateGivenData("db/testdata/seed_users.sql")
    void should_migrate_user_statuses(DataSource dataSource, Catalog catalog) throws Exception {
        SchemaCrawlerAssertions.assertThat(catalog)
                .table("app_schema", "users")
                .column("status")
                .isNotNull();

        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT status FROM app_schema.users LIMIT 1")) {
            assertThat(rs.next()).isTrue();
            assertThat(rs.getString("status")).isEqualTo("ACTIVE");
        }
    }
}
```

---

## 💾 Data Seeding Rules
- **Execution Timing**: `@TrymigrateGivenData` executes **before** the migration file of the target version is run.
- **Resource Syntax**: Do not use the `classpath:` prefix in the path (use `db/testdata/seed.sql`, not `classpath:db/testdata/seed.sql`).
- **SQL Constraints**: Always use schema-qualified names (e.g., `INSERT INTO app_schema.users ...`) because connection default schemas might not be set during execution.

---

## 🛡️ Lint Quality Gates & Suppressions
- **Verification Points**: Each test method targets a specific schema version. Lint checks only verify **new** lints introduced in this version compared to the previous verification point.
- **HTML Reports**: Reports are generated in `target/trymigrate-lint-reports/{schema}/{version}.html`.
- `@TrymigrateVerifyLints(failOn = Severity)`: Fails the test if new lints exceed the specified severity threshold (`low`, `medium`, `high`, `critical`).
- `@TrymigrateExcludeLint`: Suppresses specific linters globally for the entire test class.
- `@TrymigrateSuppressLint`: Suppresses specific linters locally for a single migration target version.

---

## 🔌 Advanced Customization Plugins
Register custom configurations locally in test classes using `@TrymigrateRegisterPlugin`:

1.  **`TrymigrateFlywayCustomizer`**: Configure Flyway options (schemas, locations, placeholders).
2.  **`TrymigrateLintersConfigurer`**: Fluently configure linter rules, severities, inclusion patterns, or register custom providers.
    ```java
    @TrymigrateRegisterPlugin
    private final TrymigrateLintersConfigurer linterConfig = config -> config
        .configure("schemacrawler.tools.linter.LinterTableWithNoRemarks")
            .severity(LintSeverity.high)
            .tableInclusionPattern("app_schema\\..*")
        .disable("schemacrawler.tools.linter.LinterTableSql");
    ```
3.  **`TrymigrateCatalogCustomizer`**: Customize the database catalog crawl limit options (e.g., filter schemas using regular expressions).
4.  **`TrymigrateDataLoader`**: Handle custom test data formats (e.g., CSV, JSON) or DBMS-specific fast loading hooks.
5.  **`TrymigrateDatabase`**: Provide static JDBC credentials when avoiding automatic lifecycle management:
    ```java
    @TrymigrateRegisterPlugin
    private final TrymigrateDatabase database = TrymigrateDatabase.of(
            "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1", null, null);
    ```

---

## ⚙️ SchemaCrawler Assertion Reference
Using `SchemaCrawlerAssertions` to verify schema definitions:

> [!WARNING]
> Column assertions change the Fluent AssertJ context. You **must terminate** column assertion chains with checking methods (e.g., `.isNotNull()`, `.matchesColumnDataTypeName(...)`, or `.isPartOfPrimaryKey(...)`) to trigger the assertions.

```java
import static java.util.function.Predicate.isEqual;

SchemaCrawlerAssertions.assertThat(catalog)
        .table("schema_name", "table_name")
        .column("id")
        .matchesColumnDataTypeName(isEqual("serial"));
```

### PostgreSQL Data Type Reference:
- `SERIAL` -> `serial`
- `INTEGER` -> `int4`
- `UUID` or `CHAR(n)` -> `bpchar`
- `TIMESTAMP WITH TIME ZONE` -> `timestamptz`

---

## 🔌 AI-Assisted "Inner Loop" Sandbox Workflow
To avoid starting and stopping database containers repeatedly during development, configure tests to run against a persistent local database sandbox (e.g., via Docker Compose):

1.  **Bypass lifecycle**: Set JVM system properties to disable container management and direct connections:
    - `-Dtrymigrate.database.lifecycle.enabled=false`
    - `-Dtrymigrate.database.url=jdbc:postgresql://localhost:5432/my_sandbox`
    - `-Dtrymigrate.database.user=postgres`
    - `-Dtrymigrate.database.password=secret`
2.  **Warmup**: Execute tests once to populate the database with the current schema state.
3.  **Iterate**: Create, modify, and test schema migrations iteratively in real-time.

---

## ⚠️ Troubleshooting & Common Pitfalls
- **Stale Migration Cached Files**: When adding, editing, or deleting `.sql` files in resources, Maven targets can store stale cache files. Always execute `mvn clean` if migrations behave unexpectedly.
- **Database Pollution**: Test methods in the same class share database state. Use `@TrymigrateCleanBefore` to reset the database prior to executing a test.
- **Clean Configuration**: Flyway 9+ disables schema clean operations by default. To use `@TrymigrateCleanBefore`, you must set `.cleanDisabled(false)` on your registered `TrymigrateFlywayCustomizer`.
- **Chronological Execution**: Migrations run cumulatively. Ensure all migration steps are run sequentially without omissions to prevent Flyway checksum or validation conflicts.
