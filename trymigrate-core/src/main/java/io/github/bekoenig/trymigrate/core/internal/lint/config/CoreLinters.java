package io.github.bekoenig.trymigrate.core.internal.lint.config;

import io.github.bekoenig.trymigrate.core.lint.config.TrymigrateLintersConfiguration;
import io.github.bekoenig.trymigrate.core.lint.config.TrymigrateLintersCustomizer;

public class CoreLinters implements TrymigrateLintersCustomizer {

    @Override
    public void accept(TrymigrateLintersConfiguration configuration) {
        configuration
                .enable("schemacrawler.tools.linter.LinterColumnTypes")
                .enable("schemacrawler.tools.linter.LinterForeignKeyMismatch")
                .enable("schemacrawler.tools.linter.LinterForeignKeySelfReference")
                .enable("schemacrawler.tools.linter.LinterForeignKeyWithNoIndexes")
                .enable("schemacrawler.tools.linter.LinterNullColumnsInIndex")
                .enable("schemacrawler.tools.linter.LinterNullIntendedColumns")
                .enable("schemacrawler.tools.linter.LinterRedundantIndexes")
                .enable("schemacrawler.tools.linter.LinterTableAllNullableColumns")
                .enable("schemacrawler.tools.linter.LinterTableCycles")
                .enable("schemacrawler.tools.linter.LinterTableEmpty")
                .enable("schemacrawler.tools.linter.LinterTableWithBadlyNamedColumns")
                .enable("schemacrawler.tools.linter.LinterTableWithIncrementingColumns")
                .enable("schemacrawler.tools.linter.LinterTableWithNoIndexes")
                .enable("schemacrawler.tools.linter.LinterTableWithNoPrimaryKey")
                .enable("schemacrawler.tools.linter.LinterTableWithNoRemarks")
                .enable("schemacrawler.tools.linter.LinterTableWithNoSurrogatePrimaryKey")
                .enable("schemacrawler.tools.linter.LinterTableWithPrimaryKeyNotFirst")
                .enable("schemacrawler.tools.linter.LinterTableWithQuotedNames")
                .enable("schemacrawler.tools.linter.LinterTableWithSingleColumn")
                .enable("schemacrawler.tools.linter.LinterTooManyLobs");
    }

}
