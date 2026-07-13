package io.github.bekoenig.trymigrate.core;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Architecture tests that enforce the package structure of trymigrate-core.
 *
 * <p>The codebase is divided into two zones:
 *
 * <pre>
 *  ┌──────────────────────────────────────────────────────────────────────────┐
 *  │  PUBLIC API  (stable, documented, no internal references)               │
 *  │                                                                          │
 *  │  [annotations]       io.github.bekoenig.trymigrate.core                │
 *  │                      Core annotations — entry point for test authors.   │
 *  │                      @Trymigrate is the composition root and the ONLY   │
 *  │                      allowed reference from the public API into the      │
 *  │                      internal engine (via @ExtendWith).                 │
 *  │                                                                          │
 *  │  [lint-annotations]  io.github.bekoenig.trymigrate.core.lint            │
 *  │                      Lint quality-gate annotations.                     │
 *  │                                                                          │
 *  │  [plugin-spi]        io.github.bekoenig.trymigrate.core.plugin          │
 *  │                      Root SPI marker and plugin registration.           │
 *  │                                                                          │
 *  │  [plugin-customize]  io.github.bekoenig.trymigrate.core.plugin.        │
 *  │                        customize                                         │
 *  │                      Extension-point interfaces only — no impls.        │
 *  └──────────────────────────────┬───────────────────────────────────────────┘
 *                                 │ implemented / consumed by
 *  ┌──────────────────────────────▼───────────────────────────────────────────┐
 *  │  INTERNAL ENGINE  (io.github.bekoenig.trymigrate.core.internal.*)        │
 *  │  Not part of the public contract. Subject to change without notice.     │
 *  │                                                                          │
 *  │  [internal-lifecycle]     JUnit lifecycle hooks; wires everything.      │
 *  │    └─► [internal-plugin-registry]  SPI discovery and priority ranking.  │
 *  │    └─► [internal-migrate]          Central Flyway/lint coordinator.     │
 *  │              └─► [internal-catalog]    SchemaCrawler crawling.           │
 *  │              └─► [internal-lint]       Lint engine and reporting.        │
 *  │              └─► [internal-callback]   Flyway callbacks.                 │
 *  │              └─► [internal-data]       Data loading.                    │
 *  │              └─► [internal-database]   Database lifecycle.               │
 *  │  [internal-parameter]     JUnit parameter injection (DataSource etc.)   │
 *  │    └─► [internal-store]   JUnit store helper.                           │
 *  │    └─► [internal-migrate]                                               │
 *  │  [internal-compatibility] Near-zero-downtime snapshot contracts.        │
 *  └──────────────────────────────────────────────────────────────────────────┘
 * </pre>
 *
 * @since 1.4.2
 */
@DisplayName("Package structure")
class PackageStructureTest {

    // ── Package constants ─────────────────────────────────────────────────────

    private static final String ROOT = "io.github.bekoenig.trymigrate.core";

    /** Public API: top-level annotations. */
    static final String PKG_ANNOTATIONS = ROOT;

    /** Public API: lint quality-gate annotations. */
    static final String PKG_LINT_ANNOTATIONS = ROOT + ".lint";

    /** Public API: root SPI marker and plugin registration annotations. */
    static final String PKG_PLUGIN_SPI = ROOT + ".plugin";

    /** Public API: extension-point interfaces — no implementation classes allowed. */
    static final String PKG_PLUGIN_CUSTOMIZE = ROOT + ".plugin.customize";

    /** Internal: root — JUnit store helper. */
    static final String PKG_INTERNAL = ROOT + ".internal";

    /** Internal: SPI plugin discovery and registry. */
    static final String PKG_INTERNAL_PLUGIN = ROOT + ".internal.plugin";

    /** Internal: database lifecycle and Testcontainers bridge. */
    static final String PKG_INTERNAL_DATABASE = ROOT + ".internal.database..";

    /** Internal: SchemaCrawler catalog crawling. */
    static final String PKG_INTERNAL_CATALOG = ROOT + ".internal.catalog";

    /** Internal: SQL/resource data loading. */
    static final String PKG_INTERNAL_DATA = ROOT + ".internal.data";

    /** Internal: lint engine, linter config, and reporters. */
    static final String PKG_INTERNAL_LINT = ROOT + ".internal.lint..";

    /** Internal: Flyway callback implementations. */
    static final String PKG_INTERNAL_CALLBACK = ROOT + ".internal.migrate.callback";

    /** Internal: central migration coordinator. */
    static final String PKG_INTERNAL_MIGRATE = ROOT + ".internal.migrate";

    /** Internal: JUnit lifecycle hooks — the wiring point. */
    static final String PKG_INTERNAL_LIFECYCLE = ROOT + ".internal.lifecycle";

    /** Internal: JUnit parameter injection. */
    static final String PKG_INTERNAL_PARAMETER = ROOT + ".internal.parameter";

    // ── Test setup ────────────────────────────────────────────────────────────

    private static JavaClasses classes;

    @BeforeAll
    static void importClasses() {
        classes = new ClassFileImporter()
                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
                .importPackages(ROOT);
    }

    // ── Public API isolation rules ────────────────────────────────────────────

    @Test
    @DisplayName("GIVEN [lint-annotations] WHEN checking dependencies THEN it must not depend on internal packages")
    void lintAnnotationsMustNotDependOnInternalPackages() {
        ArchRule rule = ArchRuleDefinition
                .noClasses().that().resideInAPackage(PKG_LINT_ANNOTATIONS)
                .should().dependOnClassesThat().resideInAPackage(PKG_INTERNAL + "..")
                .as("[lint-annotations] must not depend on internal packages");
        rule.check(classes);
    }

    @Test
    @DisplayName("GIVEN [plugin-spi] WHEN checking dependencies THEN it must not depend on internal packages")
    void pluginSpiMustNotDependOnInternalPackages() {
        ArchRule rule = ArchRuleDefinition
                .noClasses().that().resideInAPackage(PKG_PLUGIN_SPI)
                .should().dependOnClassesThat().resideInAPackage(PKG_INTERNAL + "..")
                .as("[plugin-spi] must not depend on internal packages");
        rule.check(classes);
    }

    @Test
    @DisplayName("GIVEN [plugin-customize] WHEN checking dependencies THEN it must not depend on internal packages")
    void pluginCustomizeMustNotDependOnInternalPackages() {
        ArchRule rule = ArchRuleDefinition
                .noClasses().that().resideInAPackage(PKG_PLUGIN_CUSTOMIZE)
                .should().dependOnClassesThat().resideInAPackage(PKG_INTERNAL + "..")
                .as("[plugin-customize] must not depend on internal packages");
        rule.check(classes);
    }

    @Test
    @DisplayName("GIVEN public API classes WHEN checking dependencies THEN only @Trymigrate may reference internal packages")
    void onlyCompositionRootMayReferenceInternalPackages() {
        // @Trymigrate is the single composition root that wires @ExtendWith lifecycle extensions.
        // No other public API class should depend on any internal implementation.
        ArchRule rule = ArchRuleDefinition
                .noClasses().that().resideInAPackage(PKG_ANNOTATIONS)
                .and().doNotBelongToAnyOf(Trymigrate.class)
                .should().dependOnClassesThat().resideInAPackage(PKG_INTERNAL + "..")
                .as("Only @Trymigrate (composition root) may depend on internal packages;"
                        + " all other [annotations] classes must be free of internal references");
        rule.check(classes);
    }

    @Test
    @DisplayName("GIVEN [plugin-customize] WHEN checking types THEN it contains only interfaces and annotations")
    void pluginCustomizeLayerMustContainOnlyInterfacesAndAnnotations() {
        ArchRule rule = ArchRuleDefinition
                .noClasses().that().resideInAPackage(PKG_PLUGIN_CUSTOMIZE)
                .and().areNotInterfaces()
                .and().areNotAnnotations()
                .and().areNotEnums()
                .should().bePublic()
                .as("[plugin-customize] must contain only interfaces and annotations;"
                        + " no public implementation classes are allowed");
        rule.check(classes);
    }

    // ── Internal engine dependency-direction rules ────────────────────────────

    @Test
    @DisplayName("GIVEN [internal-catalog] WHEN checking dependencies THEN it must not depend on other internal packages")
    void internalCatalogMustNotDependOnOtherInternalPackages() {
        ArchRule rule = ArchRuleDefinition
                .noClasses().that().resideInAPackage(PKG_INTERNAL_CATALOG)
                .should().dependOnClassesThat().resideInAPackage(PKG_INTERNAL + "..")
                .as("[internal-catalog] must only use the public API (plugin-customize); no internal deps");
        rule.check(classes);
    }

    @Test
    @DisplayName("GIVEN [internal-data] WHEN checking dependencies THEN it must not depend on other internal packages")
    void internalDataMustNotDependOnOtherInternalPackages() {
        ArchRule rule = ArchRuleDefinition
                .noClasses().that().resideInAPackage(PKG_INTERNAL_DATA)
                .should().dependOnClassesThat().resideInAPackage(PKG_INTERNAL + "..")
                .as("[internal-data] must only use the public API; no internal deps");
        rule.check(classes);
    }

    @Test
    @DisplayName("GIVEN [internal-lint] WHEN checking dependencies THEN it must not depend on other internal packages")
    void internalLintMustNotDependOnOtherInternalPackages() {
        // The lint subsystem (lint, lint.config, lint.report) is cohesive;
        // its sub-packages may reference each other. No external internal deps.
        ArchRule rule = ArchRuleDefinition
                .noClasses().that().resideInAPackage(PKG_INTERNAL_LINT)
                .should().dependOnClassesThat()
                .resideInAnyPackage(
                        PKG_INTERNAL,
                        PKG_INTERNAL_PLUGIN,
                        PKG_INTERNAL_DATABASE,
                        PKG_INTERNAL_CATALOG,
                        PKG_INTERNAL_DATA,
                        PKG_INTERNAL_CALLBACK,
                        PKG_INTERNAL_MIGRATE,
                        PKG_INTERNAL_LIFECYCLE,
                        PKG_INTERNAL_PARAMETER)
                .as("[internal-lint] must only use the public API and its own sub-packages; no other internal deps");
        rule.check(classes);
    }

    @Test
    @DisplayName("GIVEN [internal-database] WHEN checking dependencies THEN it must not depend on other internal packages")
    void internalDatabaseMustNotDependOnOtherInternalPackages() {
        // The database subsystem (database, database.container) is cohesive;
        // sub-packages may reference each other. No external internal deps.
        ArchRule rule = ArchRuleDefinition
                .noClasses().that().resideInAPackage(PKG_INTERNAL_DATABASE)
                .should().dependOnClassesThat()
                .resideInAnyPackage(
                        PKG_INTERNAL,
                        PKG_INTERNAL_PLUGIN,
                        PKG_INTERNAL_CATALOG,
                        PKG_INTERNAL_DATA,
                        PKG_INTERNAL_LINT,
                        PKG_INTERNAL_CALLBACK,
                        PKG_INTERNAL_MIGRATE,
                        PKG_INTERNAL_LIFECYCLE,
                        PKG_INTERNAL_PARAMETER)
                .as("[internal-database] must only use the public API and its own sub-packages; no other internal deps");
        rule.check(classes);
    }

    @Test
    @DisplayName("GIVEN [internal-callback] WHEN checking dependencies THEN it must only use allowed internal packages")
    void internalCallbackDependencies() {
        // [internal-callback] may access: plugin-customize, internal-lint,
        //   internal-catalog, internal-compatibility.
        // It must NOT access: internal-plugin-registry, internal-database,
        //   internal-data, internal-migrate, internal-lifecycle, internal-parameter, internal-store.
        ArchRule rule = ArchRuleDefinition
                .noClasses().that().resideInAPackage(PKG_INTERNAL_CALLBACK)
                .should().dependOnClassesThat().resideInAnyPackage(
                        PKG_INTERNAL_PLUGIN,
                        PKG_INTERNAL_DATABASE,
                        PKG_INTERNAL_DATA,
                        PKG_INTERNAL_MIGRATE,
                        PKG_INTERNAL_LIFECYCLE,
                        PKG_INTERNAL_PARAMETER,
                        PKG_INTERNAL)
                .as("[internal-callback] must not depend on [internal-plugin-registry],"
                        + " [internal-database], [internal-data], [internal-migrate],"
                        + " [internal-lifecycle], [internal-parameter], or [internal-store]");
        rule.check(classes);
    }

    @Test
    @DisplayName("GIVEN [internal-plugin-registry] WHEN checking dependencies THEN it must not depend on non-db internal packages")
    void internalPluginRegistryDependencies() {
        // [internal-plugin-registry] may access: plugin-spi, plugin-customize, internal-database.
        ArchRule rule = ArchRuleDefinition
                .noClasses().that().resideInAPackage(PKG_INTERNAL_PLUGIN)
                .should().dependOnClassesThat().resideInAnyPackage(
                        PKG_INTERNAL,
                        PKG_INTERNAL_CATALOG,
                        PKG_INTERNAL_DATA,
                        PKG_INTERNAL_LINT,
                        PKG_INTERNAL_CALLBACK,
                        PKG_INTERNAL_MIGRATE,
                        PKG_INTERNAL_LIFECYCLE,
                        PKG_INTERNAL_PARAMETER)
                .as("[internal-plugin-registry] may only access [plugin-spi], [plugin-customize],"
                        + " and [internal-database]; no other internal packages");
        rule.check(classes);
    }

    @Test
    @DisplayName("GIVEN [internal-migrate] WHEN checking dependencies THEN it must not depend on lifecycle or parameter packages")
    void internalMigrateMustNotDependOnLifecycleOrParameter() {
        ArchRule rule = ArchRuleDefinition
                .noClasses().that().resideInAPackage(PKG_INTERNAL_MIGRATE)
                .and().resideOutsideOfPackage(PKG_INTERNAL_CALLBACK)
                .should().dependOnClassesThat().resideInAnyPackage(
                        PKG_INTERNAL_LIFECYCLE,
                        PKG_INTERNAL_PARAMETER,
                        PKG_INTERNAL)
                .as("[internal-migrate] must not depend on [internal-lifecycle],"
                        + " [internal-parameter], or [internal-store]");
        rule.check(classes);
    }

    @Test
    @DisplayName("GIVEN [internal-parameter] WHEN checking dependencies THEN it may only access internal-store and internal-migrate")
    void internalParameterDependencies() {
        ArchRule rule = ArchRuleDefinition
                .noClasses().that().resideInAPackage(PKG_INTERNAL_PARAMETER)
                .should().dependOnClassesThat().resideInAnyPackage(
                        PKG_INTERNAL_PLUGIN,
                        PKG_INTERNAL_DATABASE,
                        PKG_INTERNAL_CATALOG,
                        PKG_INTERNAL_DATA,
                        PKG_INTERNAL_LINT,
                        PKG_INTERNAL_CALLBACK,
                        PKG_INTERNAL_LIFECYCLE)
                .as("[internal-parameter] may only access [internal-store] and [internal-migrate]");
        rule.check(classes);
    }

    @Test
    @DisplayName("GIVEN [internal-lifecycle] WHEN checking dependencies THEN it must not depend on parameter")
    void internalLifecycleMustNotDependOnParameter() {
        // internal-lifecycle uses internal-store (StoreSupport) to wire the processor — allowed.
        // It must NOT use internal-parameter (parameter resolvers are separate extensions).
        ArchRule rule = ArchRuleDefinition
                .noClasses().that().resideInAPackage(PKG_INTERNAL_LIFECYCLE)
                .should().dependOnClassesThat().resideInAPackage(PKG_INTERNAL_PARAMETER)
                .as("[internal-lifecycle] must not depend on [internal-parameter]");
        rule.check(classes);
    }
}
