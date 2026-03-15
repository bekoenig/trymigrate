# trymigrate | LLM Context & Architecture Blueprint

This document provides a high-density technical map of the `trymigrate` codebase for AI models and agents.

## 🎯 Core Mission
A JUnit 5 extension providing a TDD sandbox for database migrations by orchestrating **Flyway** (migration), **SchemaCrawler** (inspection/linting), and **Testcontainers** (isolation).

## 🏗️ Architectural Invariants
- **Modular Monorepo:** `trymigrate-core` contains the engine; `trymigrate-<db>` modules provide vendor-specific drivers and container configurations.
- **JUnit 5 Lifecycle:**
    - `BeforeAll`: Database initialization/Testcontainer start (`MigrateInitializer`).
    - `BeforeEach`: Per-test migration execution (`MigrateExecutor` -> `MigrateProcessor`).
    - `Parameter Resolution`: Injection of `DataSource`, `Catalog`, and `Lints`.
    - `AfterAll`: Database disposal.
- **Visibility Rules:** Internal logic MUST reside in `*.internal.*` packages. Public API is restricted to the root and specific plugin/lint packages.
- **Tech Stack:** Java 17+, Flyway 10+, SchemaCrawler 16+, JUnit 5.

## 🧩 Plugin System & Priority
Plugins are implementations of `TrymigratePlugin`.
1. **Priority 1 (Highest):** Fields annotated with `@TrymigrateRegisterPlugin`.
2. **Priority 2:** SPI discovery restricted by `@TrymigrateDiscoverPlugins(origin = ...)`.
3. **Priority 3:** General SPI discovery via `ServiceLoader`.

**Key Extension Points:**
- `TrymigrateFlywayCustomizer`: Flyway config (schemas, locations).
- `TrymigrateDatabase`: DB connection/lifecycle abstraction.
- `TrymigrateLintersConfigurer`: Fluent SchemaCrawler linter settings.
- `TrymigrateDataLoader`: Custom seed data logic.
- `TrymigrateLintsReporter`: Custom reporting (Slack, Jira, etc.).

## 🛡️ Linting & Smart Diffing
- **Mechanism:** `LintsHistory` tracks violations per version.
- **Logic:** Only **new** lints (not present in the previous analyzed version) are reported to the console and HTML.
- **Quality Gate:** `@TrymigrateVerifyLints` triggers a test failure if the delta lints exceed the `failOn` severity.

## 📁 Directory Map
- `/trymigrate-core`: The engine.
    - `src/main/java/.../core`: Primary annotations (`@Trymigrate`, etc.).
    - `src/main/java/.../core/plugin`: Extension point interfaces.
    - `src/main/java/.../core/internal`: Lifecycle executors and processors.
- `/trymigrate-<db>`: Database-specific modules.
    - Must include an `Example<DB>SchemaTest.java` for verification.

## 👩‍💻 Generative Guide for Agents

### 1. Test Skeleton (Copy & Adapt)
Use this structure when creating new migration tests.

> [!NOTE]
> **Mandatory Dependency:** To use `SchemaCrawlerAssertions`, ensure your `pom.xml` includes `io.github.bekoenig:assertj-schemacrawler`.

```java
import io.github.bekoenig.assertj.schemacrawler.api.SchemaCrawlerAssertions;
import static org.assertj.core.api.Assertions.assertThat;

@Trymigrate
@TrymigrateVerifyLints(failOn = LintSeverity.medium) // 1. Set quality gate
class UserSchemaTest {

    // 2. Register Database Container (e.g., PostgreSQL)
    @TrymigrateRegisterPlugin
    static final PostgreSQLContainer<?> db = new PostgreSQLContainer<>("postgres:16");

    // 3. Configure Flyway
    @TrymigrateRegisterPlugin
    final TrymigrateFlywayCustomizer flyway = config -> config
        .locations("classpath:db/migration")
        .defaultSchema("public")
        .cleanDisabled(false); // Required for @TrymigrateCleanBefore

    @Test
    @TrymigrateWhenTarget("1.0") // 4. Define target version
    @TrymigrateCleanBefore       // 5. Ensure fresh state (optional but recommended)
    void should_CreateUserTable(Catalog catalog, Lints lints) {
        // 6. Assert Structure
        SchemaCrawlerAssertions.assertThat(catalog)
            .table("public", "users")
            .column("email").isNotNull().hasType("varchar");
        
        // 7. Assert Lints (Full state available in parameter)
        assertThat(lints).isEmpty();
    }
}
```

### 2. Assertion Patterns
*   **Structure:** Use `SchemaCrawlerAssertions` from `assertj-schemacrawler` (fluent API for tables, columns, FKs).
*   **Lints:** Use AssertJ on the `Lints` object. `assertThat(lints).isEmpty()` checks the **full current state** (minus excludes).
*   **Data:** Use standard JDBC or `JdbcTemplate` with the injected `DataSource` to query and verify data content.

### 3. Common Pitfalls
*   **Missing `cleanDisabled(false)`:** If using `@TrymigrateCleanBefore`, Flyway config must explicitly enable cleaning or it will fail.
*   **Version Ordering:** `trymigrate` runs tests in version order. A test for "1.0" runs before "1.1". If "1.1" runs first, the DB state remains at "1.1" for subsequent tests unless `@TrymigrateCleanBefore` is used.
*   **Scope:** `Catalog` contains EVERYTHING in the schema. `Lints` contains EVERYTHING (minus excludes). The *Quality Gate* only checks the *difference*.
*   **First-Run Only Gate:** If multiple tests target the same version (e.g., with different `@TrymigrateGivenData`), the `@TrymigrateVerifyLints` quality gate is only triggered for the **first** test of that version. Subsequent tests for the same version skip the automated check but still receive the full `Lints` parameter.

## 🛠️ Development Rules
- **Javadoc:** Mandatory for all public classes and annotations. Must include `@see` links to related components.
- **Testing:** New features must be verified with an integration test using at least one database module.
- **Flyway Clean:** The `cleanDisabled(false)` configuration is required for `@TrymigrateCleanBefore` to function.
- **Naming:** Follow standard Java camelCase. Annotations MUST start with `Trymigrate...`.
- **Database Reuse:** Static fields for containers enable sharing across tests; `@TrymigrateCleanBefore` ensures isolation.
