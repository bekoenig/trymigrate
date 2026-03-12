 # Contributing to trymigrate

First off, thank you for considering contributing to **trymigrate**! It's people like you that make it a great tool.

## 🚀 Getting Started

1.  **Fork the repository** on GitHub.
2.  **Clone your fork** locally:
    ```bash
    git clone https://github.com/YOUR-USERNAME/trymigrate.git
    ```
3.  **Build the project** using Maven:
    ```bash
    ./mvnw clean install
    ```

## 🛠️ Project Structure

- `trymigrate-core`: The main JUnit 5 extension.
- `trymigrate-<db>`: Database-specific integration modules (Postgres, MySQL, etc.).
- `internal` package: Please keep internal logic separated from the public API classes.

## 📜 Coding Standards

- **Java 17**: We use modern Java features.
- **JavaDoc**: All public API classes must have descriptive JavaDoc (including @since and @see where applicable).
- **Tests**: Every new feature or bug fix must include a corresponding test in the relevant database module.
- **Format**: Follow the standard Java conventions (IntelliJ default or Google Style).

## 🐛 Reporting Issues

- Use the GitHub Issue Tracker.
- Provide a **Minimal Reproducible Example** (MRE) if reporting a bug.
- Label your issue appropriately (e.g., `bug`, `enhancement`, `database-module`).

## 💡 Pull Requests

1.  Create a new branch for your feature or fix.
2.  Ensure your code builds and all tests pass.
3.  Submit the PR against the `main` branch.
4.  Provide a clear description of the changes.

---
*By contributing, you agree that your contributions will be licensed under its MIT License.*
