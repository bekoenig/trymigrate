# GEMINI.md

## Project Overview

**trymigrate** is a JUnit 5 extension that enables Test-Driven Development (TDD) for database schema migrations. It integrates [Flyway](https://flywaydb.org/), [SchemaCrawler](https://www.schemacrawler.com/), and [Testcontainers](https://www.testcontainers.org/) to provide a sandbox for verifying migration versions iteratively.

### Main Technologies
- **Java 17+**: Core development language.
- **JUnit 5**: The testing framework and extension engine.
- **Flyway**: Manages and executes the actual database migrations.
- **SchemaCrawler**: Inspects the resulting database model and runs linters to detect bad practices.
- **Testcontainers**: Orchestrates isolated database instances for testing.
- **Maven**: Project management and build tool.
- **AssertJ / assertj-schemacrawler**: Fluent assertions for database catalogs and lints.

### Architecture
The project follows a modular, plugin-based architecture:
- `trymigrate-core`: The heart of the extension, containing the lifecycle management (`MigrateInitializer`, `MigrateExecutor`), annotation definitions, and parameter resolvers.
- `trymigrate-<db>` modules (e.g., `trymigrate-postgresql`, `trymigrate-mysql`): Provide database-specific support, including drivers and pre-configured Testcontainers.

---

## Building and Running

### Prerequisites
- Java 17 or higher.
- Docker (required by Testcontainers).

### Key Commands
- **Build and Install**:
  ```bash
  ./mvnw clean install
  ```
- **Run All Tests**:
  ```bash
  ./mvnw test
  ```
- **Run Specific Module Tests**:
  ```bash
  ./mvnw test -pl trymigrate-postgresql
  ```
- **Format Check (Inferred)**:
  The project follows standard Java conventions (IntelliJ or Google Style).
- **Check for Lint Reports**:
  After running tests, reports are generated in:
  `target/trymigrate-lint-reports/{schema}/{version}.html`

---

## Development Conventions

### Coding Style
- **Java 17 Features**: Leverage modern Java syntax (records, sealed classes where applicable).
- **Public API Documentation**: All public classes and annotations must have descriptive Javadoc, including `@since` and `@see` if relevant.
- **Internal vs. Public**: Keep internal logic within `internal` packages to maintain a clean public API.

### Testing Practices
- **Version-by-Version Verification**: Tests should typically use `@TrymigrateWhenTarget("version")` to verify specific migration states.
- **Container Management**: Use `@TrymigrateRegisterPlugin` to register database containers within test classes.
- **Assertions**: Use `SchemaCrawlerAssertions` for verifying table structures and `Lints` for checking schema quality.
- **Database Modules**: When adding support for a new database, create a new `trymigrate-<db>` module and include a comprehensive `Example<DB>SchemaTest.java`.

### Contribution Guidelines
- **Fork and PR**: Contributions should be submitted via Pull Requests against the `main` branch.
- **Minimal Reproducible Example (MRE)**: Bug reports should include an MRE.
- **Compatibility**: Ensure changes remain compatible with the minimum versions of dependencies (JUnit 5.x, Flyway 10.x+, SchemaCrawler 16.x+).
