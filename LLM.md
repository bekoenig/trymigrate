# LLM Technical Context: trymigrate

## 🚀 Core Mandate
**trymigrate** is a JUnit 5 extension for Test-Driven Development (TDD) of database schema migrations. It integrates **Flyway** (migration), **SchemaCrawler** (inspection/linting), and **AssertJ-DB** (data verification).

## 🏗️ The AI-Assisted "Inner Loop" Workflow
This workflow is designed for high-velocity development using a persistent database "sandbox" and an AI agent equipped with **MCP tools**.

### 1. Persistent Sandbox Setup
Keep a database container running (e.g., via Docker Compose) to allow real-time inspection.
- **JVM Overrides:** Use these properties to connect JUnit to the sandbox and disable trymigrate's internal container lifecycle:
  - `-Dtrymigrate.database.lifecycle.enabled=false`
  - `-Dtrymigrate.database.url=jdbc:postgresql://localhost:5432/<db>`
  - `-Dtrymigrate.database.user=<user>`
  - `-Dtrymigrate.database.password=<password>`

### 3. The Interactive TDD Cycle
**Preparation:** Run the test suite once to ensure the sandbox matches the current codebase state.

1.  **Explore (AI):** Scan `src/test/resources/db/migration` for existing files. Use MCP tools (`mcp_schemacrawler_diagram`, `list`) to understand the current sandbox state.
2.  **Align (AI/Human):** Discuss the next requirement.
3.  **Bootstrap (AI):** Create a new `@Test` method. Inject needed parameters: `Catalog` (structure), `DataSource` (data), and `Lints` (quality).
4.  **Spec (AI):** Write **comprehensive assertions**.
5.  **Validate (Human):** Run the test. It should fail.
6.  **Migrate (AI):** Create the `.sql` migration file -> go to **Step 4**.

---

## 🛠️ Implementation Details

### 1. Schema Assertions (SchemaCrawler)
Use `SchemaCrawlerAssertions`. **Crucial:** Column assertions change the context and **must terminate** with a check.
```java
assertThat(catalog).table("schema", "table")
        .column("id").matchesColumnDataTypeName(isEqual("serial"));
```
*   **Postgres Type Cheat Sheet:**
    *   `SERIAL` -> `serial`
    *   `INTEGER` -> `int4`
    *   `UUID` or `CHAR(n)` -> `bpchar`
    *   `TIMESTAMP WITH TIME ZONE` -> `timestamptz`
*   **Predicate Import:** `static java.util.function.Predicate.isEqual`.

### 2. Data Verification & Seeding
#### Seeding (@TrymigrateGivenData)
- **Execution:** Runs `BEFORE_EACH_MIGRATE`.
- **SQL Strings:** **ALWAYS** use schema-qualified names (e.g., `INSERT INTO blog.posts ...`) because the connection's default schema might not be set.
- **Resource Files:** Do NOT use the `classpath:` prefix (e.g., use `db/testdata/setup.sql`).


#### Verification (AssertJ-DB)
Inject `DataSource` to verify that data transformations (e.g., seeding, splitting columns) worked correctly.
```java
@Test
@TrymigrateWhenTarget("1.1.0")
@TrymigrateGivenData("db/testdata/pre_migrate_data.sql")
void verify_data(DataSource dataSource) {
    Table table = new Table(dataSource, "my_schema.my_table");
    assertThat(table).row(0).value("status").isEqualTo("ACTIVE");
}
```

### 3. Quality Gate (@TrymigrateVerifyLints)
Always apply `@TrymigrateVerifyLints` to catch best-practice violations (missing indices, etc.).
- **Reports:** If a lint fails, inspect the HTML report at:
  `target/trymigrate-lint-reports/<schema>/<version>.html`

---

## ⚠️ Common Pitfalls & Troubleshooting
- **Stale target classes:** When deleting/renaming `.sql` files, Maven often keeps old versions in `target/test-classes`. **Solution:** Run `mvn clean`.
- **Sandbox Sync:** If the database state is "dirty", apply `@TrymigrateCleanBefore` to the *first* test method of the suite to reset the sandbox.
- **Flyway Config:** Ensure `.cleanDisabled(false)` is set in your `TrymigrateFlywayCustomizer` if you use `@TrymigrateCleanBefore`.
- **Full Execution:** Always run the entire test class. Migrations are cumulative; skipping versions will lead to Flyway checksum or sequence errors.
