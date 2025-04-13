package io.github.bekoenig.trymigrate.core.internal.schemacrawler.lint.config;

import io.github.bekoenig.trymigrate.core.lint.config.LintersConfiguration;
import io.github.bekoenig.trymigrate.core.lint.config.LintersCustomizer;
import schemacrawler.tools.linter.*;

public class DefaultLinters implements LintersCustomizer {

    @Override
    public void accept(LintersConfiguration configuration) {
        configuration
                .register(new LinterProviderCatalogSql())
                .enable(new LinterProviderColumnTypes())
                .enable(new LinterProviderForeignKeyMismatch())
                .enable(new LinterProviderForeignKeySelfReference())
                .enable(new LinterProviderForeignKeyWithNoIndexes())
                .enable(new LinterProviderNullColumnsInIndex())
                .enable(new LinterProviderNullIntendedColumns())
                .enable(new LinterProviderRedundantIndexes())
                .enable(new LinterProviderTableAllNullableColumns())
                .enable(new LinterProviderTableCycles())
                .enable(new LinterProviderTableEmpty())
                .register(new LinterProviderTableSql())
                .enable(new LinterProviderTableWithBadlyNamedColumns())
                .enable(new LinterProviderTableWithIncrementingColumns())
                .enable(new LinterProviderTableWithNoIndexes())
                .enable(new LinterProviderTableWithNoPrimaryKey())
                .enable(new LinterProviderTableWithNoRemarks())
                .enable(new LinterProviderTableWithNoSurrogatePrimaryKey())
                .enable(new LinterProviderTableWithPrimaryKeyNotFirst())
                .enable(new LinterProviderTableWithQuotedNames())
                .enable(new LinterProviderTableWithSingleColumn())
                .enable(new LinterProviderTooManyLobs());
    }

}
