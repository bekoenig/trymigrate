# trymigrate

![trymigrate logo](https://github.com/user-attachments/assets/4af1eea5-0056-4ff7-81c0-9063727d3ce1)

**The TDD Sandbox for Database Migrations.**
*Stop guessing if your SQL works. Start verifying it—version by version, script by script.*

[![Maven Central](https://img.shields.io/maven-central/v/io.github.bekoenig.trymigrate/trymigrate-core.svg?style=flat-square)](https://search.maven.org/artifact/io.github.bekoenig.trymigrate/trymigrate-core)
[![Build Status](https://img.shields.io/github/actions/workflow/status/bekoenig/trymigrate/build-verify.yml?branch=main&style=flat-square)](https://github.com/bekoenig/trymigrate/actions/workflows/build-verify.yml)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg?style=flat-square)](https://opensource.org/licenses/MIT)
[![Java Support](https://img.shields.io/badge/Java-17%2B-orange.svg?style=flat-square)](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)

---

## 📖 Table of Contents
- [🧐 What is trymigrate?](#-what-is-trymigrate)
- [🚀 Quick Start](#-quick-start)
- [🧪 The TDD Experience](#-the-tdd-experience)
- [🛡️ Automated Quality Gates](#-automated-quality-gates)
- [💾 Data Seeding & Scenarios](#-data-seeding--scenarios)
- [🏗️ Multi-Schema Support](#️-multi-schema-support)
- [🗄️ Database Lifecycle](#️-database-lifecycle)
- [🔌 Supported Databases](#-supported-databases)
- [📖 API Reference](#-api-reference)
- [🧩 Plugin System](#-plugin-system)
- [⚙️ Configuration](#️-configuration)
- [🤝 Contributing](#-contributing)

---

## 🧐 What is trymigrate?

Database migrations are often the most fragile part of an application. **trymigrate** turns the "black box" of SQL scripts into a testable, versioned, and linted part of your codebase. It orchestrates [Flyway](https://flywaydb.org/), [SchemaCrawler](https://www.schemacrawler.com/), and [Testcontainers](https://www.testcontainers.org/) into a seamless JUnit 5 extension.

> **Treat your schema like code.** Write a test, write the SQL, and verify the exact state of your database at any version.

---

## 📋 Prerequisites

Before you start, ensure you have the following installed:
*   **Java 17** or higher.
*   **Docker** (required by Testcontainers for orchestrating database instances).

---

## 🚀 Quick Start

1. **Add the Dependencies** (e.g., for PostgreSQL):
```xml
<dependency>
    <groupId>io.github.bekoenig.trymigrate</groupId>
    <artifactId>trymigrate-postgresql</artifactId>
    <version>${trymigrate.version}</version>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>testcontainers-postgresql</artifactId>
    <version>${testcontainers.version}</version>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>io.github.bekoenig</groupId>
    <artifactId>assertj-schemacrawler</artifactId>
    <version>${assertj-schemacrawler.version}</version>
    <scope>test</scope>
</dependency>
```

`trymigrate-postgresql` provides the extension itself together with Flyway PostgreSQL support, the JDBC driver, and SchemaCrawler integration.
`testcontainers-postgresql` and `assertj-schemacrawler` are shown separately because the example uses both APIs directly.

2. **Write Your First Test**:
```java
@Trymigrate // 1. Activate the extension
class MySchemaTest {

    // 2. Register your database container
    @TrymigrateRegisterPlugin
    final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16");

    @Test
    @TrymigrateWhenTarget("1.0") // 3. Set the target version
    void should_HaveCorrectUserTable(Catalog catalog) {
        // 4. Assert your schema state
        SchemaCrawlerAssertions.assertThat(catalog).table("public", "users").column("email").isNotNull();
    }
}
```

---

## 🧪 The TDD Experience

`trymigrate` encourages an iterative workflow. Verify that version `1.0` provides a solid foundation before evolving to `1.1`. 

### Available Parameters
Test methods can receive the following parameters automatically:

| Parameter | Type | Purpose |
| :--- | :--- | :--- |
| `Catalog` | `schemacrawler.schema.Catalog` | The full database model for structural assertions. |
| `Lints` | `schemacrawler.tools.lint.Lints` | All current schema violations (Full State minus global excludes). |
| `DataSource` | `javax.sql.DataSource` | A live connection to the test database for data-level assertions. |

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
        SchemaCrawlerAssertions.assertThat(catalog).table("app_schema", "users")
            .column("id").isPartOfPrimaryKey(true);
        SchemaCrawlerAssertions.assertThat(catalog).table("app_schema", "users")
            .column("email").isNotNull();
    }

    @Test
    @TrymigrateWhenTarget("1.1")
    @TrymigrateGivenData("INSERT INTO app_schema.users (id, email) VALUES (gen_random_uuid(), 'test@example.com');")
    void should_MigrateDataSafety(Catalog catalog, Lints lints) {
        // Verify migration 1.1 didn't break existing data or constraints
        SchemaCrawlerAssertions.assertThat(catalog).table("app_schema", "users").column("last_login").isNotNull();
        assertThat(lints).isEmpty(); // No new regressions
    }
}
```

---

## 🛡️ Automated Quality Gates

Every migration version is automatically inspected for architectural anti-patterns using SchemaCrawler's powerful linting engine.

*   **Verification Points & Smart Diffing:** Each test method acts as a **Verification Point**. The automated quality gate only reports **new** lints introduced since the previous Verification Point. Legacy issues are filtered out to keep your focus sharp.
*   **Complete State Visibility:** While the quality gate focuses on the delta, the injected `Lints` parameter always provides the **full current state** (minus global excludes). This allows you to verify if existing violations were fixed or transformed.

*   **Intermediate Reporting:** Even if a test method skips several migration versions (e.g., from `1.0` to `1.5`), `trymigrate` still performs linting and generates reports for every intermediate migration version. You can find these in `target/trymigrate-lint-reports/`.
*   **The Gatekeeper:** Use `@TrymigrateVerifyLints` to fail tests if new violations (e.g., missing primary keys, bad naming) exceed your severity threshold. Supported levels are: `low`, `medium`, `high`, `critical`.
*   **Multi-Test Efficiency:** If multiple tests target the same version, the quality gate is only checked for the **first test** (according to execution order). Subsequent tests for the same version do not need to repeat the check as the version has already been verified.

### Suppressing & Excluding
Handle legacy debt or edge cases with granular control:
*   **`@TrymigrateExcludeLint`**: Globally ignore specific rules or objects for the entire test class.
*   **`@TrymigrateSuppressLint`**: Locally allow specific lints for a single migration version without removing them from the reports.

### Custom Linter Configuration
For advanced needs, use `TrymigrateLintersConfigurer` to fluently configure, enable, or disable specific linters. This allows you to override default severities, restrict linters to specific naming patterns, or **register custom linter providers without SPI**:

```java
@TrymigrateRegisterPlugin
private final TrymigrateLintersConfigurer linterConfig = config -> config
    .register(new MyCustomLinterProvider()) // Register a custom linter
    .configure("schemacrawler.tools.linter.LinterTableWithNoRemarks") // Configure an existing linter
        .severity(LintSeverity.high) // Set severity to high
        .tableInclusionPattern("app_schema\\..*") // Apply only to tables starting with "APP_"
    .disable("schemacrawler.tools.linter.LinterTableSql"); // Disable a specific linter
```

**Default Linting & Scope:**
*   **Defaults:** By default, trymigrate enables a curated set of SchemaCrawler linters suitable for migration testing.
*   **Flyway History:** The Flyway schema history table is **automatically excluded** from linting to prevent false positives. However, it remains present in the `Catalog`.
*   **Schema Scope:** Linting is strictly limited to schemas **managed by Flyway**. It is not possible to extend linting to schemas outside of Flyway's control.

> [!TIP]
> You can also register custom linters via the standard Java SPI mechanism by adding your `LinterProvider` implementation to `META-INF/services/schemacrawler.tools.lint.LinterProvider`.

---

## 💾 Data Seeding & Scenarios

Testing migrations often requires more than just an empty schema. `trymigrate` allows you to seed data at specific points in the timeline to verify transformation logic or handle data-dependent constraints.

### The Role of `@TrymigrateGivenData`
This annotation seeds data **immediately before** the migration script of the target version is executed. It supports two formats by default:
*   **Raw SQL Strings:** Directly execute statements like `INSERT INTO ...`.
*   **Classpath Resources:** Provide a path to a file ending in `.sql` (e.g., `db/testdata/baseline.sql`).

Important behavior:
*   The built-in SQL loader expects a plain classpath resource path such as `db/testdata/baseline.sql`, not `classpath:db/testdata/baseline.sql`.
*   Data loading only happens if trymigrate actually migrates *to* the target version. If the database is already at or above that version, the test fails. Use `@TrymigrateCleanBefore` when you need a guaranteed fresh baseline.
*   Seeded SQL is executed directly through JDBC, outside Flyway's SQL script processing. Flyway placeholders and related script features are therefore not applied.

*   **Initial Inventory (Baseline Data):** Seed representative, production-like records into version `1.0` to verify that your `1.1` migration script correctly handles data transformations (e.g., migrating a `JSON` blob into structured columns).
*   **Scenario-based Testing:** Load specific datasets to test edge cases, such as very large tables or "dirty" data, before a `NOT NULL` or `UNIQUE` constraint is applied.

### Custom Loaders with `TrymigrateDataLoader`
While `trymigrate` handles SQL out of the box, you can implement `TrymigrateDataLoader` to support custom formats or **DBMS-specific loading mechanisms**:

```java
@TrymigrateRegisterPlugin
private final TrymigrateDataLoader postgresCopyLoader = new TrymigrateDataLoader() {
    @Override
    public boolean supports(String resource, String extension, TrymigrateDatabase database) {
        return "pgbin".equals(extension); // Trigger for .pgbin files
    }

    @Override
    public void load(String resource, Connection connection, TrymigrateDatabase database) {
        // Use the native PostgreSQL COPY API for massive speed
    }
};
```

---

## 🏗️ Multi-Schema Support

Building enterprise-grade architectures? `trymigrate` handles complex databases with multiple schemas natively:

*   **Isolated Reporting:** Reports are organized by schema for a clear audit trail.
*   **Cross-Schema Assertions:** Verify foreign keys and joins that span multiple schemas using a single injected `Catalog`.
*   **Selective Enforcement:** Enforce strict standards in new schemas while being lenient with legacy ones.

### Catalog Customization
By default, `trymigrate` uses Flyway's schema configuration (e.g., `flyway.defaultSchema` or `flyway.schemas`) to determine which schemas to crawl. You can provide custom implementations of `TrymigrateCatalogCustomizer` to override or refine this behavior.

> [!NOTE]
> The `Catalog` includes **all** discovered objects within the configured schemas, including the Flyway migration history table (e.g., `flyway_schema_history`). This differs from linting, which excludes the history table by default.

**Example:**
```java
// In your test class:
@TrymigrateRegisterPlugin
private final TrymigrateCatalogCustomizer catalogCustomizer = new TrymigrateCatalogCustomizer() {
    @Override
    public void customize(LimitOptionsBuilder builder) {
        // Override default schema selection and include only schemas starting with "APP_"
        // and exclude those starting with "SYS_" using SchemaCrawler's RegularExpressionRule.
        builder.includeSchemas(new RegularExpressionRule("APP_.*", "SYS_.*"));
    }
};
```

---

## 🗄️ Database Lifecycle

Efficiently manage your database state across test suites.

### 🐳 Testcontainers Integration
Any `JdbcDatabaseContainer` annotated with `@TrymigrateRegisterPlugin` is automatically managed:
*   **Instance Field:** The container is started before and **stopped after** the test class.
*   **Static Field:** The container is **not stopped** after the test class. It is shared across multiple test classes for maximum performance and only disposed of when the JVM exits.

### 🧼 Fresh State with `@TrymigrateCleanBefore`
The database instance is reused for all test methods within a class. Data seeded in one method remains visible in subsequent ones. Use `@TrymigrateCleanBefore` to trigger a `flyway clean` before a test starts to ensure a fresh state (especially when seeding data or testing specific versions in isolation).

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
| `@TrymigrateCleanBefore` | Method | Wipes the database before the current migration version. |
| `@TrymigrateVerifyLints` | Class | Configures the quality gate severity threshold. |
| `@TrymigrateExcludeLint` | Class | Globally **drops** specific lints (hidden from reports and quality gate). |
| `@TrymigrateSuppressLint` | Method | Locally **allows** specific lints (visible in reports, ignored by quality gate). |
| `@TrymigrateRegisterPlugin` | Field | Registers customizers or Testcontainers. |
| `@TrymigrateDiscoverPlugins`| Class | Fine-tunes SPI-based plugin discovery. |

---

## 🧩 Plugin System

`trymigrate` is a modular engine. Extend almost every aspect through these public extension points:

| Interface | Purpose |
| :--- | :--- |
| `TrymigrateFlywayCustomizer` | Configure Flyway (locations, placeholders, schemas). |
| `TrymigrateLintersConfigurer` | Fluently configure SchemaCrawler linters (Severity, Regex). |
| `TrymigrateCatalogCustomizer` | Customize the database crawl (filter types, schemas). |
| `TrymigrateLintOptionsCustomizer` | Customize general SchemaCrawler text output options (for example database info, JDBC driver info, or unqualified names). |
| `TrymigrateDataLoader` | Support custom data formats (CSV, JSON, etc.). |
| `TrymigrateDatabase` | Abstraction for custom DB lifecycle/connection. |
| `TrymigrateLintsReporter` | Send lint results to Slack, Jira, or custom tools. |

### Registration & Hierarchy

Plugins can be registered in two ways:

1.  **Local Registration (`@TrymigrateRegisterPlugin`):** Register a field directly in your test class. The field can implement one of the supported extension interfaces such as `TrymigrateFlywayCustomizer`, `TrymigrateDataLoader`, `TrymigrateLintsReporter`, `Callback`, or `JavaMigration`. Local registrations have the **highest priority**.
2.  **Global Registration (Java SPI):** Register a class in `META-INF/services/io.github.bekoenig.trymigrate.core.plugin.TrymigratePlugin`. SPI-discovered plugins must implement `TrymigratePlugin` directly or through a database-specific marker interface such as `TrymigratePostgreSQLPlugin`.

Only one `TrymigrateDatabase` can be active for a test class. Registering multiple databases, whether locally, via SPI, or mixed, fails fast during plugin resolution.

### Discovery & Control

Fine-tune how global plugins are discovered using `@TrymigrateDiscoverPlugins`:

*   **Selective Loading:** Use `@TrymigrateDiscoverPlugins(origin = TrymigratePostgreSQLPlugin.class)` to only load plugins belonging to a specific hierarchy (for example, PostgreSQL-specific SPI plugins).
*   **Exclusion:** Use `@TrymigrateDiscoverPlugins(exclude = {LegacyLinter.class, GenericReporter.class})` to explicitly block certain plugins or entire interface groups from being loaded.

Database-specific marker interfaces such as `TrymigratePostgreSQLPlugin` or `TrymigrateH2Plugin` are primarily useful for SPI plugins. They let you group global plugins by database family and select them with `origin`.

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

| Datenbank | Modul |
| :--- | :--- |
| DB2 | `trymigrate-db2` |
| H2 | `trymigrate-h2` |
| HSQLDB | `trymigrate-hsqldb` |
| MariaDB | `trymigrate-mariadb` |
| MySQL | `trymigrate-mysql` |
| Oracle | `trymigrate-oracle` |
| PostgreSQL | `trymigrate-postgresql` |
| SQL Server | `trymigrate-sqlserver` |
    
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

*Bringing the power of TDD to the foundation of your application.*

> [!NOTE]
> This documentation was created with the support of AI to ensure clarity and technical precision.
