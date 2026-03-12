<p align="center">
  <img src="https://github.com/user-attachments/assets/4af1eea5-0056-4ff7-81c0-9063727d3ce1" alt="trymigrate logo" width="600">
</p>

<h1 align="center">trymigrate</h1>

<p align="center">
  <strong>The TDD Sandbox for Database Migrations.</strong><br>
  <em>Stop guessing if your SQL works. Start verifying it—step by step, version by version.</em>
</p>

<p align="center">
  <a href="https://search.maven.org/artifact/io.github.bekoenig.trymigrate/trymigrate-core"><img src="https://img.shields.io/maven-central/v/io.github.bekoenig.trymigrate/trymigrate-core.svg?style=flat-square" alt="Maven Central"></a>
  <a href="https://github.com/bekoenig/trymigrate/actions/workflows/build-verify.yml"><img src="https://img.shields.io/github/actions/workflow/status/bekoenig/trymigrate/build-verify.yml?branch=main&style=flat-square" alt="Build Status"></a>
  <a href="https://opensource.org/licenses/MIT"><img src="https://img.shields.io/badge/License-MIT-yellow.svg?style=flat-square" alt="License: MIT"></a>
  <a href="https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html"><img src="https://img.shields.io/badge/Java-17%2B-orange.svg?style=flat-square" alt="Java Support"></a>
</p>

---

## 📖 Table of Contents
- [🧐 What is trymigrate?](#-what-is-trymigrate)
- [🧪 The TDD Experience](#-the-tdd-experience)
- [🛡️ Automated Quality Gates](#-automated-quality-gates)
- [🏗️ Multi-Schema Support](#️-multi-schema-support)
- [🗄️ Database Lifecycle](#️-database-lifecycle)
- [🔌 Supported Databases](#-supported-databases)
- [📖 API Reference](#-api-reference)
- [🧩 Plugin System](#-plugin-system)
- [⚙️ Configuration](#️-configuration)

---

## 🧐 What is trymigrate?

Database migrations are often the most fragile part of an application. **trymigrate** turns the "black box" of SQL scripts into a testable, versioned, and linted part of your codebase. It orchestrates [Flyway](https://flywaydb.org/), [SchemaCrawler](https://www.schemacrawler.com/), and [Testcontainers](https://www.testcontainers.org/) into a seamless JUnit 5 extension.

> **Treat your schema like code.** Write a test, write the SQL, and verify the exact state of your database at any version.

---

## 🧪 The TDD Experience

`trymigrate` encourages an iterative workflow. Verify that version `1.0` provides a solid foundation before evolving to `1.1`. Parameters like `Catalog`, `DataSource`, and `Lints` are automatically injected into your test methods.

```java
@Trymigrate
@TrymigrateVerifyLints(failOn = LintSeverity.critical)
class SchemaEvolutionTest {

    // 1. Define your container (managed automatically)
    @TrymigrateRegisterPlugin
    private final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16");

    // 2. Optional: Configure Flyway (schemas, locations, etc.)
    @TrymigrateRegisterPlugin
    private final TrymigrateFlywayCustomizer flyway = config -> config
        .defaultSchema("app_schema")
        .locations("classpath:db/migration")
        .cleanDisabled(false);

    @Test
    @TrymigrateWhenTarget("1.0")
    void should_EstablishBaseline(Catalog catalog) {
        assertThat(catalog).table("app_schema", "users")
            .column("id").isPrimaryKey().hasType("uuid")
            .column("email").isNotNull();
    }

    @Test
    @TrymigrateWhenTarget("1.1")
    @TrymigrateGivenData("INSERT INTO app_schema.users (id, email) VALUES (gen_random_uuid(), 'test@example.com');")
    void should_MigrateDataSafety(Catalog catalog, Lints lints) {
        // Verify migration 1.1 didn't break existing data or constraints
        assertThat(catalog).table("app_schema", "users").hasColumn("last_login");
        assertThat(lints).isEmpty(); // No new regressions
    }
}
```

---

## 🛡️ Automated Quality Gates

Every migration step is automatically inspected for architectural anti-patterns using SchemaCrawler's powerful linting engine.

*   **Smart Diffing:** `trymigrate` only reports **new** lints introduced by the current migration version. Legacy issues are filtered out to keep your focus sharp.
*   **The Gatekeeper:** Use `@TrymigrateVerifyLints` to fail tests if new violations (e.g., missing primary keys, bad naming) exceed your severity threshold.
*   **Observability:** Lints are logged to the console and saved as detailed HTML reports in `target/trymigrate-lint-reports/{schema}/{version}.html`.

### Suppressing & Excluding
Handle legacy debt or edge cases with granular control:
*   **`@TrymigrateExcludeLint`**: Globally ignore specific rules or objects for the entire test class.
*   **`@TrymigrateSuppressLint`**: Locally allow specific lints for a single migration step without removing them from the reports.

### Custom Linter Configuration
For advanced needs, use `TrymigrateLintersConfigurer` to fluently configure, enable, or disable specific linters. This allows you to override default severities, restrict linters to specific naming patterns, or **register custom linters without SPI**:

```java
@TrymigrateRegisterPlugin
private final TrymigrateLintersConfigurer linterConfig = config -> config
    .register(new MyCustomLinterProvider()) // Register directly!
    .configure("schemacrawler.tools.linter.LinterTableWithNoRemarks")
        .severity(LintSeverity.high)
        .tableInclusionPattern("app_schema\\..*")
    .disable("schemacrawler.tools.linter.LinterTableSql");
```

> [!TIP]
> You can also register custom linters via the standard Java SPI mechanism by adding your `LinterProvider` implementation to `META-INF/services/schemacrawler.tools.lint.LinterProvider`.

---

## 🏗️ Multi-Schema Support

Building enterprise-grade architectures? `trymigrate` handles complex databases with multiple schemas natively:

*   **Isolated Reporting:** Reports are organized by schema for a clear audit trail.
*   **Cross-Schema Assertions:** Verify foreign keys and joins that span multiple schemas using a single injected `Catalog`.
*   **Selective Enforcement:** Enforce strict standards in new schemas while being lenient with legacy ones.

```java
@TrymigrateRegisterPlugin
private final TrymigrateFlywayCustomizer flyway = config -> config
    .defaultSchema("app_core")
    .schemas("app_audit", "app_reporting")
    .cleanDisabled(false); // Enable for @TrymigrateCleanBefore support
```

---

## 🗄️ Database Lifecycle

Efficiently manage your database state across test suites.

### 🐳 Testcontainers Integration
Any `JdbcDatabaseContainer` annotated with `@TrymigrateRegisterPlugin` is automatically managed:
*   **Instance Field:** The container is started before and **stopped after** the test class.
*   **Static Field:** The container is **not stopped** after the test class. It is shared across multiple test classes for maximum performance and only disposed of when the JVM exits.

### 🧼 Fresh State with `@TrymigrateCleanBefore`
When using **static fields** (shared containers) or testing non-incremental scenarios, the database will likely contain "leftovers" from previous runs. Use `@TrymigrateCleanBefore` to trigger a `flyway clean` before the current test's migration starts.
> [!IMPORTANT]  
> Flyway 9+ disables `clean` by default. You must set `.cleanDisabled(false)` in your `TrymigrateFlywayCustomizer` for this to work.

---

## 📖 API Reference

### Annotations
| Annotation | Scope | Purpose |
| :--- | :--- | :--- |
| `@Trymigrate` | Class | Entry point. Activates the extension. |
| `@TrymigrateWhenTarget` | Method | Sets the Flyway version to migrate to. Supports `"latest"`. |
| `@TrymigrateGivenData` | Method | Seeds SQL or custom data *before* the target migration. |
| `@TrymigrateCleanBefore` | Method | Wipes the database before the current migration step. |
| `@TrymigrateVerifyLints` | Class | Configures the quality gate severity threshold. |
| `@TrymigrateExcludeLint` | Class | Globally ignores specific lints (supports Regex). |
| `@TrymigrateSuppressLint` | Method | Locally allows specific lints for a migration step. |
| `@TrymigrateRegisterPlugin` | Field | Registers customizers or Testcontainers. |
| `@TrymigrateDiscoverPlugins`| Class | Fine-tunes SPI-based plugin discovery. |

---

## 🧩 Plugin System

`trymigrate` is a modular engine. Extend almost every aspect by implementing these interfaces:

| Interface | Purpose |
| :--- | :--- |
| `TrymigrateFlywayCustomizer` | Configure Flyway (locations, placeholders, schemas). |
| `TrymigrateLintersConfigurer` | Fluently configure SchemaCrawler linters (Severity, Regex). |
| `TrymigrateCatalogCustomizer` | Customize the database crawl (filter types, schemas). |
| `TrymigrateDataLoader` | Support custom data formats (CSV, JSON, etc.). |
| `TrymigrateDatabase` | Abstraction for custom DB lifecycle/connection. |
| `TrymigrateLintsReporter` | Send lint results to Slack, Jira, or custom tools. |

---

## ⚙️ Configuration

| JVM Property | Description | Default |
| :--- | :--- | :--- |
| `trymigrate.container.db-port` | Pin host port for local debugging (e.g. `5432:5432`). | Random |
| `trymigrate.lint.report.html.skip-empty` | Only generate HTML reports if lints are found. | `true` |
| `trymigrate.lint.report.html.basedir` | Root directory for reports. | `target/` |

---

## 🔌 Supported Databases

Trymigrate provides pre-configured modules for all major databases:

<table>
  <tr>
    <td align="center"><b>PostgreSQL</b><br><code>trymigrate-postgresql</code></td>
    <td align="center"><b>MySQL</b><br><code>trymigrate-mysql</code></td>
    <td align="center"><b>MariaDB</b><br><code>trymigrate-mariadb</code></td>
    <td align="center"><b>SQL Server</b><br><code>trymigrate-sqlserver</code></td>
  </tr>
  <tr>
    <td align="center"><b>Oracle</b><br><code>trymigrate-oracle</code></td>
    <td align="center"><b>DB2</b><br><code>trymigrate-db2</code></td>
    <td align="center"><b>HSQLDB</b><br><code>trymigrate-hsqldb</code></td>
    <td align="center">...and more</td>
  </tr>
</table>

---

## 🤝 Contributing

We love contributions! Whether it's a bug report, a new database module, or a feature request.

> [!IMPORTANT]  
> **By executing tests for enterprise modules** (e.g., DB2 or SQL Server), you programmatically accept the respective vendor's license agreements, as the test code explicitly triggers the acceptance (e.g., via `.acceptLicense()`). Ensure you are familiar with the vendor's terms before running these tests.

1.  **Fork** the repository.
2.  **Build**: `./mvnw clean install`
3.  Check [CONTRIBUTING.md](CONTRIBUTING.md) for details.

---

## ⚖️ License

Distributed under the **MIT License**. See `LICENSE` for more information.

---
<p align="center">
  <i>Bringing the power of TDD to the foundation of your application.</i>
</p>
