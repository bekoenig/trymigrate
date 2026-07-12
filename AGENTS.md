# AGENTS.md: Developer Context for `trymigrate` Core Development

This document provides architectural context, development guidelines, and testing procedures for developers and AI agents working on the **trymigrate** codebase itself.

---

## 🎭 Role

You are an **open-source developer** with a focus on **testing database migrations**. You contribute to and maintain the `trymigrate` library, ensuring robust, reliable, and well-tested migration workflows across supported database engines. Your expertise spans JUnit 5 extensions, Flyway, SchemaCrawler, Testcontainers, and schema linting. You advocate for high test coverage, clear public APIs, and a smooth contributor experience.

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

## 📋 Git & PR Conventions

- **Branch naming**: `feat/`, `fix/`, `docs/`, `refactor/` prefixes.
- **Commit messages**: Conventional Commits format — `<type>(<scope>): <summary>` (e.g., `feat(core): add new lint rule`).
- **CI**: PRs to `main` run `mvn clean verify` on JDK 17 (Temurin). Pushes to `main` deploy snapshots.

---

## ⚠️ Troubleshooting Core Tests
- **Cached Test Resources**: Maven can keep stale Flyway migration files or test classes in the `target/` directories. Always run `mvn clean` if migrations are behaving unexpectedly.
- **Classpath Replacer**: In `trymigrate-core`, some tests verify plugin discovery by dynamically modifying classpaths. Ensure the `classpath-replacer` test dependency is not broken during build upgrades.

---

## 📖 Glossary (Ubiquitous Language)

Use these terms consistently in code, docs, and commit messages.

### Annotations
| Term | Meaning |
|------|---------|
| `@Trymigrate` | Class-level annotation activating the JUnit 5 extension for migration testing. |
| `@TrymigrateWhenTarget` | Sets the Flyway migration version a test method migrates to (e.g., `"1.0"`, `"latest"`). |
| `@TrymigrateGivenData` | Seeds SQL data or loads a classpath resource before the target migration executes. |
| `@TrymigrateCleanBefore` | Triggers `flyway clean` before the test for a fresh database state. |
| `@TrymigrateVerifyLints` | Configures the lint quality gate severity threshold (`low`, `medium`, `high`, `critical`). |
| `@TrymigrateExcludeLint` | Globally drops specific linters from reports and quality gate. |
| `@TrymigrateSuppressLint` | Locally allows specific lints for one version (visible in reports, ignored by quality gate). |
| `@TrymigrateRegisterPlugin` | Field-level annotation registering a local plugin in the test class. |
| `@TrymigrateDiscoverPlugins` | Controls SPI plugin discovery via `origin` (marker interface filter) and `exclude`. |

### Plugin Interfaces
| Term | Meaning |
|------|---------|
| `TrymigratePlugin` | Root SPI interface for Java ServiceLoader discovery. |
| `TrymigrateDatabase` | Provides database connection and lifecycle (`prepare`/`dispose`). Only one active per test. |
| `TrymigrateFlywayCustomizer` | Configures Flyway options (schemas, locations, placeholders, etc.). |
| `TrymigrateCatalogCustomizer` | Customizes SchemaCrawler crawl scope via `LimitOptionsBuilder`. |
| `TrymigrateLintersConfigurer` | Configures linter rules: severity, inclusion patterns, custom `LinterProvider` instances. |
| `TrymigrateLintOptionsCustomizer` | Customizes SchemaCrawler text output options. |
| `TrymigrateDataLoader` | Custom data loading (CSV, JSON, bulk). Implements `supports()` and `load()`. |
| `TrymigrateLintsReporter` | Sends lint results to external systems (files, Slack, Jira, etc.). |

### Domain Concepts
| Term | Meaning |
|------|---------|
| **Catalog** | SchemaCrawler `Catalog` object representing the full crawled database structure at the current migration state. |
| **Lints** | Collection of schema quality violations detected by linter rules at the current migration step. |
| **Quality Gate** | Automated mechanism that fails a test if new lints exceed the configured severity threshold. |
| **Verification Point** | A test method targeting a migration version. The quality gate reports only *new* lints since the previous verification point (via smart diffing). |
| **Smart Diffing** | The mechanism that compares lints between consecutive verification points, so only *newly introduced* violations trigger the quality gate — pre-existing lints are ignored. |
| **Intermediate Reporting** | Even when jumping versions, lint reports are generated for every intermediate migration in `target/trymigrate-lint-reports/`. |
| **Local Plugin** | Plugin registered via `@TrymigrateRegisterPlugin` on a test class field. Highest priority. |
| **Global Plugin (SPI)** | Plugin discovered via ServiceLoader from `META-INF/services/...TrymigratePlugin`. |
| **Database Lifecycle** | The `prepare`/`dispose` cycle for the test database, managed automatically for Testcontainers. |
| **Customizer** | A functional interface plugin that modifies configuration before execution. |

---

## 🔄 Agent Harness: Feedback Loop

The harness must allow agents to verify their own work immediately after making changes, with structured retry on failure.

### Verification Commands by Task Type

| Task Type | Verification Command | Fail-fast? |
|-----------|---------------------|------------|
| Any code change | `mvn compile -pl <module>` | Yes |
| New lint rule | `mvn test -pl trymigrate-core` | No |
| New DB module | `mvn test -pl trymigrate-<db>` | No |
| POM changes | `mvn validate` | Yes |
| Public API change | `mvn javadoc:javadoc -pl <module>` | No |

### Retry Protocol

1. Agent completes a task and signals "done".
2. Harness runs the verification command(s) for the task type.
3. On failure:
   - Feed **exit code**, **stdout** (last 50 lines), and **stderr** (last 50 lines) back to the agent.
   - Increment retry counter.
   - Agent must fix the issue and signal "done" again.
4. **Retry budget**: 3 attempts. After exhaustion, mark the task as failed and report to the user.
5. On success: mark task as verified and proceed.

### Graduated Verification Order

```
1. mvn compile -pl <module>        → fast, catches syntax/type errors
2. mvn test-compile -pl <module>   → catches test compilation issues
3. mvn test -pl <module>           → full correctness check
```

Abort at the first failing stage — do not run integration tests if compilation fails.

---

## 🧩 Agent Harness: Task Decomposition

Agents must not rely on ad-hoc planning. The harness provides structured task graphs for common workflows.

### Task Templates

#### Template: `add-db-module`
```yaml
name: add-db-module
params: [db_name]
steps:
  - id: create-submodule
    action: "Create trymigrate-{db_name} module with POM inheriting from parent"
    verify: "mvn validate -pl trymigrate-{db_name}"
  - id: implement-marker
    depends_on: [create-submodule]
    action: "Create Trymigrate{Db}Plugin marker interface extending TrymigratePlugin"
    verify: "mvn compile -pl trymigrate-{db_name}"
  - id: implement-database
    depends_on: [implement-marker]
    action: "Implement TrymigrateDatabase with Testcontainers lifecycle"
    verify: "mvn compile -pl trymigrate-{db_name}"
  - id: register-spi
    depends_on: [implement-database]
    action: "Add META-INF/services entry for the plugin"
    verify: "mvn compile -pl trymigrate-{db_name}"
  - id: write-example-test
    depends_on: [register-spi]
    action: "Create Example{Db}SchemaTest.java with migration + lint verification"
    verify: "mvn test -pl trymigrate-{db_name}"
```

#### Template: `add-lint-rule`
```yaml
name: add-lint-rule
params: [rule_name, severity]
steps:
  - id: implement-linter
    action: "Create LinterProvider implementation for {rule_name}"
    verify: "mvn compile -pl trymigrate-core"
  - id: register-linter
    depends_on: [implement-linter]
    action: "Register in default linter configuration"
    verify: "mvn compile -pl trymigrate-core"
  - id: write-test
    depends_on: [register-linter]
    action: "Add test case verifying the lint triggers correctly"
    verify: "mvn test -pl trymigrate-core"
  - id: document
    depends_on: [write-test]
    action: "Add Javadoc and update glossary if needed"
    verify: "mvn javadoc:javadoc -pl trymigrate-core"
```

### Decomposition Rules

- **Checkpoints**: After each step verifies successfully, the harness persists the state. On later failure, resume from the last passing checkpoint.
- **Parallelism**: Steps without `depends_on` relationships may execute concurrently.
- **Scope locking**: Once a template is instantiated, new discovered work becomes a separate task — never expand a running step's scope.
- **Max steps per task**: 8. If decomposition exceeds this, split into multiple tasks.

---

## 🛡️ Agent Harness: Guard Rails & Assertion Hooks

Structural enforcement of project conventions. These run automatically before any agent output is accepted.

### File Permission Model

| Path Pattern | Permission | Notes |
|-------------|-----------|-------|
| `pom.xml` (any module) | `require-approval` | Version/dependency changes need human review |
| `META-INF/services/*` | `write` | But must match an existing plugin interface |
| `src/main/**/internal/**` | `write` | Internal implementation, no API constraints |
| `src/main/**` (non-internal) | `guarded` | Public API — triggers assertion hooks below |
| `.github/**` | `read-only` | CI config is off-limits |

### Assertion Hooks

These checks run on every file the agent modifies, **before** verification commands:

#### 1. Public API Assertions (triggered on `guarded` paths)
- [ ] New public classes/interfaces have `@since` Javadoc tag
- [ ] New public methods have Javadoc with `@param` and `@return`
- [ ] No implementation details (private helpers, utility methods) in public packages

#### 2. SPI Consistency
- [ ] If a class implements `TrymigratePlugin`, a corresponding `META-INF/services` entry exists
- [ ] Only one `TrymigrateDatabase` implementation per module

#### 3. Naming Conventions
- [ ] Test classes match `*Test.java` or `Example*Test.java`
- [ ] Plugin classes match `Trymigrate*Plugin.java` or `*Customizer.java`
- [ ] Database modules named `trymigrate-<lowercase-db>`

#### 4. Structural Limits
- [ ] **Max files modified per task**: 12 — exceeding this suggests wrong approach
- [ ] **Max lines added per file**: 300 — large additions likely need decomposition
- [ ] **No deletions of public API** without explicit user approval

### Hook Failure Protocol

When an assertion hook fails:
1. Block the commit/output.
2. Report which assertion(s) failed with specific file and line references.
3. Agent must fix violations before re-attempting.
4. Hook failures do **not** consume the retry budget (they are pre-verification).
