# Contributing to `trymigrate`

First off, thank you for considering contributing to **trymigrate**! Contributions from the community help make this tool robust and developer-friendly.

This guide focuses on the high-level contribution process, git workflows, and issue reporting. For in-depth technical details about the library's architecture, submodules, Java SPI plugin design, and code conventions, please refer to the **[AGENTS.md](AGENTS.md)** developer guide.

---

## 🚀 Getting Started

### Prerequisites
Before you start, make sure you have installed:
- **Java 17 or 21**
- **Maven** (system-wide installation)
- **Docker** (required for container integration tests)

### Forking and Cloning
1. **Fork** the repository on GitHub.
2. **Clone** your fork locally:
   ```bash
   git clone https://github.com/YOUR-USERNAME/trymigrate.git
   cd trymigrate
   ```
3. **Verify** the setup by building the codebase:
   ```bash
   mvn clean install
   ```

---

## 🛠️ The Git & Contribution Workflow

To keep the repository history clean and manageable, we follow a structured git workflow:

### 1. Branch Naming Conventions
Always create a descriptive branch for your changes starting from the `main` branch. Use the following prefix guidelines:
- `feature/` or `feat/` for new features (e.g., `feat/add-cockroachdb-support`)
- `bugfix/` or `fix/` for bug fixes (e.g., `fix/postgres-unqualified-names`)
- `docs/` for documentation updates (e.g., `docs/update-readme`)
- `refactor/` for code refactoring with no behavior changes

### 2. Commit Message Guidelines
We encourage commit messages that follow the **Conventional Commits** specification:
```
<type>(<scope>): <short summary>

[optional body]
```
- **Types**: `feat`, `fix`, `docs`, `style`, `refactor`, `test`, `chore`.
- **Scope**: Usually the module name (e.g., `core`, `postgresql`, `h2`, `deps`).
- **Example**: `feat(postgresql): add support for postgresql 18.x container defaults`

### 3. Pull Request Lifecycle
1. Ensure all tests compile and pass locally:
   ```bash
   mvn clean test
   ```
2. Push the changes to your GitHub fork.
3. Open a Pull Request (PR) against the `main` branch of the official repository.
4. Fill out the PR template with a clear description of the problem solved and the implementation approach.
5. **Code Review & Feedback**: A maintainer will review your PR. Address comments, update the branch if needed, and push modifications directly to your branch.
6. Once the CI build succeeds and the code is approved, a maintainer will merge your PR.

---

## 🐛 Reporting Issues and Feature Requests

### Creating Bug Reports
If you find a bug, please check the existing issues to see if it has already been reported. If not, open a new issue and include:
- A clear, descriptive title.
- Steps to reproduce the issue.
- Expected vs. actual behavior.
- Environment details (OS, JDK version, Database flavor, `trymigrate` version).
- A **Minimal Reproducible Example (MRE)**. The most helpful MRE is a small test class reproducing the failure.

### Requesting Features
To suggest a new feature or database engine support:
- Describe the problem it solves and why it is useful.
- Provide examples of the proposed API or configuration style.

---

## ⚖️ License
By contributing, you agree that your contributions will be licensed under the project's **MIT License**.
