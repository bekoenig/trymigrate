# AGENTS.md: Developer Context for `trymigrate` Core Development

This document provides architectural context, development guidelines, and testing procedures for developers and AI agents working on the **trymigrate** codebase itself.

---

## 🚀 Core Mandate
Your goal is to maintain, optimize, and expand the `trymigrate` JUnit 5 extension library. All changes should respect the modular, plugin-based architecture and keep the public APIs clean.

---

## 🏗️ Project Architecture & Layout

`trymigrate` uses a parent POM with several submodules:

*   **`trymigrate-core`**: The main JUnit 5 extension engine.
    - Contains the lifecycle extensions (`MigrateInitializer`, `MigrateExecutor`).
    - Implements parameter resolvers for `DataSource`, `Catalog`, and `Lints`.
    - Coordinates customizers and plugins.
    - Resolves and reports schema lints.
*   **`trymigrate-<db>` modules** (e.g., `trymigrate-postgresql`, `trymigrate-mysql`): Database-specific integrations.
    - Pre-configures specific Testcontainers and JDBC drivers.
    - Implements database-specific SPI plugins to customize crawling, linting, and default behaviors for that database family.

---

## 🔌 Plugin System & Service Provider Interface (SPI)

The extension relies on a Java SPI-based plugin mechanism. When extending database support or writing custom features internally:

1.  **Core Interface**: Plugins must implement `io.github.bekoenig.trymigrate.core.plugin.TrymigratePlugin`.
2.  **Marker Interfaces**: Database-specific SPI modules use sub-interfaces (e.g., `TrymigratePostgreSQLPlugin`, `TrymigrateH2Plugin`) to allow conditional discovery and isolation via `@TrymigrateDiscoverPlugins(origin = ...)`.
3.  **Local vs SPI Resolution**:
    - Local plugins are registered via `@TrymigrateRegisterPlugin` in the test class.
    - Global plugins are discovered via Java SPI by listing them in `META-INF/services/io.github.bekoenig.trymigrate.core.plugin.TrymigratePlugin`.
4.  **Database Plugins**: Only one active `TrymigrateDatabase` plugin can be resolved per test execution. Registering multiple database plugins causes a fast-fail exception during test class initialization.

---

## 🛠️ Building, Compiling and Testing

### Commands
Always compile and test using the system `mvn` command directly:
- **Build and Install all modules**:
  ```bash
  mvn clean install
  ```
- **Run All Tests**:
  ```bash
  mvn test
  ```
- **Test a Specific Module**:
  ```bash
  mvn test -pl trymigrate-postgresql
  ```

### 🐳 Testcontainers and Vendor Licenses
- A running Docker daemon is required for running database container integration tests.
- **Enterprise DBs (DB2, Oracle, SQL Server)**: Tests in these modules programmatically accept license terms (e.g., calling `.acceptLicense()`). Ensure compliance with respective vendor licenses before executing these tests.

---

## ⚙️ Development Conventions

1.  **Coding Style**:
    - Target Java 17 features (records, pattern matching, sealed classes where appropriate).
    - Maintain strict separation of internal and public APIs.
2.  **Internal Packages**:
    - Put all implementation-specific classes under `io.github.bekoenig.trymigrate.core.internal.*`.
    - Keep the public API packages (`io.github.bekoenig.trymigrate.core`, `io.github.bekoenig.trymigrate.core.lint`, etc.) clean of implementation details.
3.  **Javadoc Requirements**:
    - All public API classes, builders, and annotations must have descriptive Javadoc.
    - Use `@since` tags to indicate new additions.
4.  **Creating New Database Modules**:
    - When adding support for a new database engine, create a new `trymigrate-<db>` submodule.
    - Make sure it implements a database-specific marker interface extending `TrymigratePlugin`.
    - Include a comprehensive integration test named `Example<DB>SchemaTest.java` that verifies its default container, Flyway integration, and schema crawling.

---

## ⚠️ Troubleshooting Core Tests
- **Cached Test Resources**: Maven can keep stale Flyway migration files or test classes in the `target/` directories. Always run `mvn clean` if migrations are behaving unexpectedly.
- **Classpath Replacer**: In `trymigrate-core`, some tests verify plugin discovery by dynamically modifying classpaths. Ensure the `classpath-replacer` test dependency is not broken during build upgrades.
