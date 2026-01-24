package io.github.bekoenig.trymigrate.core.internal.lint.config;

import io.github.bekoenig.trymigrate.core.plugin.TrymigratePlugin;
import io.github.bekoenig.trymigrate.core.plugin.customize.TrymigrateLintersConfigurer;

public class CoreLinters implements TrymigrateLintersConfigurer, TrymigratePlugin {

    @Override
    public void accept(TrymigrateLintersConfiguration configuration) {
        configuration
                .configure("schemacrawler.tools.linter.LinterColumnTypes")
                .configure("schemacrawler.tools.linter.LinterForeignKeyMismatch")
                .configure("schemacrawler.tools.linter.LinterForeignKeySelfReference")
                .configure("schemacrawler.tools.linter.LinterForeignKeyWithNoIndexes")
                .configure("schemacrawler.tools.linter.LinterNullColumnsInIndex")
                .configure("schemacrawler.tools.linter.LinterNullIntendedColumns")
                .configure("schemacrawler.tools.linter.LinterRedundantIndexes")
                .configure("schemacrawler.tools.linter.LinterTableAllNullableColumns")
                .configure("schemacrawler.tools.linter.LinterTableCycles")
                .configure("schemacrawler.tools.linter.LinterTableEmpty")
                .configure("schemacrawler.tools.linter.LinterTableWithBadlyNamedColumns")
                .configure("schemacrawler.tools.linter.LinterTableWithIncrementingColumns")
                .configure("schemacrawler.tools.linter.LinterTableWithNoIndexes")
                .configure("schemacrawler.tools.linter.LinterTableWithNoPrimaryKey")
                .configure("schemacrawler.tools.linter.LinterTableWithNoRemarks")
                .configure("schemacrawler.tools.linter.LinterTableWithNoSurrogatePrimaryKey")
                .configure("schemacrawler.tools.linter.LinterTableWithPrimaryKeyNotFirst")
                .configure("schemacrawler.tools.linter.LinterTableWithQuotedNames")
                .configure("schemacrawler.tools.linter.LinterTableWithSingleColumn")
                .configure("schemacrawler.tools.linter.LinterTooManyLobs");
    }

}
