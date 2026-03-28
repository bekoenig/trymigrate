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
  - `TrymigrateDatabase`: Provides the test database lifecycle and connection details.

---

## 🏗️ Testing Patterns

### 1. Comprehensive Schema Test (TDD Pattern)
```java
@Trymigrate
@TrymigrateVerifyLints(failOn = LintSeverity.medium) // 1. Set quality gate
class UserSchemaTest {

    @TrymigrateRegisterPlugin
    static final PostgreSQLContainer<?> db = new PostgreSQLContainer<>("postgres:16");

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
