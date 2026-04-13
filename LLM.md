# LLM Technical Context: trymigrate

## 🚀 Core Mandate
**trymigrate** is a JUnit 5 extension for Test-Driven Development (TDD) of database schema migrations. It integrates **Flyway** (migration), **SchemaCrawler** (inspection/linting), and **Testcontainers** (isolation).

## 🧩 Key Architecture
- `trymigrate-core`: Lifecycle management (`MigrateInitializer`, `MigrateExecutor`), annotations, and parameter resolvers.
- `trymigrate-<db>`: Database-specific support (e.g., PostgreSQL, MySQL).
- **Core Interfaces (Plugins):**
  - `TrymigrateFlywayCustomizer`: Configures Flyway (locations, schemas).
  - `TrymigrateLintersConfigurer`: Configures SchemaCrawler linters.
  - `TrymigrateCatalogCustomizer`: Customizes the database crawl (filters).
  - `TrymigrateLintOptionsCustomizer`: Customizes general SchemaCrawler text output options.
  - `TrymigrateDataLoader`: Custom data seeding (SQL, CSV, etc.).
  - `TrymigrateLintsReporter`: Reports new lint deltas after each analyzed migration.
  - `TrymigrateDatabase`: Provides the test database lifecycle and connection details. Use `TrymigrateDatabase.of(...)` for static JDBC connections without a custom implementation.

---

## 🏗️ Testing Patterns

### 1. Comprehensive Schema Test (TDD Pattern)
```java
@Trymigrate
@TrymigrateVerifyLints(failOn = LintSeverity.medium) // 1. Set quality gate
class UserSchemaTest {

    @TrymigrateRegisterPlugin
    static final PostgreSQLContainer<?> db = new PostgreSQLContainer<>("postgres:18");

    @Test
    @TrymigrateWhenTarget("1.1.0")
    // Use resource paths OR raw Inline SQL:
    @TrymigrateGivenData({
        "db/testdata/initial_users.sql", 
        "INSERT INTO user_mgmt.users (id, name) VALUES ('user-1', 'Inlined User')"
    })
    void should_HaveCorrectProfile(Catalog catalog) {
        SchemaCrawlerAssertions.assertThat(catalog).table("user_mgmt", "users").column("name").isNotNull();
    }
}
```

### 2. Assertion Patterns
*   **Structural Verification:** ALWAYS use `SchemaCrawlerAssertions`.
*   **Quality Standards (Lints):** Controlled via `@TrymigrateVerifyLints`.
*   **Data Verification:** Use the injected `DataSource` for content-level checks.

### 3. Common Pitfalls & Knowledge
*   **Inline SQL vs. Scripts:** `@TrymigrateGivenData` supports both. If no DataLoader (e.g., for `.sql`) matches the string, it is executed as raw SQL via JDBC.
*   **Resource Paths:**
    *   `TrymigrateFlywayCustomizer`: **REQUIRES** the `classpath:` prefix for locations.
    *   `@TrymigrateGivenData`: The prefix requirement depends on the **used DataLoader**.
        *   **Default SQL Loader** (`.sql`): Expects a raw classpath path **WITHOUT** the `classpath:` prefix.
*   **Plugin Discovery:** If a test behaves unexpectedly, check for global SPI plugins. Use `@TrymigrateDiscoverPlugins(exclude = ...)` to disable them locally.
*   **Full Class Execution:** ALWAYS run the entire test class. Migrations build upon each other (1.0 -> 1.1 -> 1.2). Running a single method (e.g., via `-Dtest=Class#method`) skips earlier verifications and might hide issues in the migration path.
*   **Database Lifecycle:** Within a test class, the **same database instance is reused** for all `@Test` methods. Data seeded in one method remains for the next. **ALWAYS use `@TrymigrateCleanBefore`** if you need a fresh state for a specific migration version or to avoid `DuplicateKeyException` when seeding data.
*   **Static Containers:** A `static` container field keeps the database alive **beyond the current test class**, allowing other test classes to reuse it for better performance. Instance-level containers are stopped after the class.
*   **Single Database Plugin:** Exactly one `TrymigrateDatabase` may be active per test class. Multiple registered databases fail during plugin resolution.
*   **Scope:** The *Quality Gate* only checks the **difference** (delta) introduced by the current migration version.

## 🛠️ Development Rules
- **Javadoc:** Mandatory for all public APIs (classes, annotations).
- **Naming:** Annotations MUST start with `Trymigrate...`.
- **Database Reuse:** Static containers enable sharing; `@TrymigrateCleanBefore` ensures isolation.

---

## 🚀 AI-Assisted Inner Loop (TDD Workflow)

You can use **trymigrate** in a high-velocity "Inner Loop" where a local database container stays alive, and an AI agent (equipped with a **SchemaCrawler MCP server**) explores the schema to help you write migrations.

### 1. Start a Persistent Sandbox
Launch your database container manually (or via Docker Compose) to keep it alive between test runs:
```bash
docker run -d --name trymigrate-sandbox \
  -p 5432:5432 \
  -e POSTGRES_DB=testdb -e POSTGRES_USER=admin -e POSTGRES_PASSWORD=secret \
  postgres:18
```

### 2. Configure Your AI Agent (MCP)
Point your AI's **SchemaCrawler MCP server** to this persistent sandbox. This allows the AI to "see" the tables, constraints, and lints in real-time.

### 3. Run Tests with Overrides
Use the **trymigrate JVM properties** to connect your JUnit tests to the running sandbox and **disable the lifecycle** (so trymigrate doesn't try to start/stop its own container):

```bash
mvn test -Dtest=MySchemaTest \
  -Dtrymigrate.database.lifecycle.enabled=false \
  -Dtrymigrate.database.url=jdbc:postgresql://localhost:5432/testdb \
  -Dtrymigrate.database.user=admin \
  -Dtrymigrate.database.password=secret
```

### 4. The Loop
1.  **Code:** You write a failing test (e.g., `@TrymigrateWhenTarget("2.0")`). **Pro-Tip:** Apply `@TrymigrateCleanBefore` to the test method with the **lowest** `@TrymigrateWhenTarget` (e.g., your '1.0' migration). This ensures the persistent sandbox is reset once at the start of your test suite. **Note:** This requires `cleanDisabled(false)` in your `TrymigrateFlywayCustomizer`.
2.  **Execute:** Run the test with the properties above. Flyway migrates the sandbox.
3.  **Explore:** Ask the AI: *"Check the current schema in the sandbox. Why did my join fail?"*
4.  **Fix:** The AI uses the MCP server to inspect the catalog, identifies a missing index or column, and proposes the next `.sql` migration.
5.  **Repeat:** Save the migration and run the test again.
