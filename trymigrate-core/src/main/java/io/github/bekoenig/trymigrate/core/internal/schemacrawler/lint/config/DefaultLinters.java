package io.github.bekoenig.trymigrate.core.internal.schemacrawler.lint.config;

import io.github.bekoenig.trymigrate.core.lint.config.LintersConfiguration;
import schemacrawler.tools.linter.*;

import java.util.function.Consumer;

public class DefaultLinters implements Consumer<LintersConfiguration> {

    @Override
    public void accept(LintersConfiguration configuration) {
        configuration
                .register(new LinterProviderCatalogSql())
                .configure(new LinterProviderColumnTypes())
                .configure(new LinterProviderForeignKeyMismatch())
                .configure(new LinterProviderForeignKeySelfReference())
                .configure(new LinterProviderForeignKeyWithNoIndexes())
                .configure(new LinterProviderNullColumnsInIndex())
                .configure(new LinterProviderNullIntendedColumns())
                .configure(new LinterProviderRedundantIndexes())
                .configure(new LinterProviderTableAllNullableColumns())
                .configure(new LinterProviderTableCycles())
                .configure(new LinterProviderTableEmpty())
                .register(new LinterProviderTableSql())
                .configure(new LinterProviderTableWithBadlyNamedColumns())
                .configure(new LinterProviderTableWithIncrementingColumns())
                .configure(new LinterProviderTableWithNoIndexes())
                .configure(new LinterProviderTableWithNoPrimaryKey())
                .configure(new LinterProviderTableWithNoRemarks())
                .configure(new LinterProviderTableWithNoSurrogatePrimaryKey())
                .configure(new LinterProviderTableWithPrimaryKeyNotFirst())
                .configure(new LinterProviderTableWithQuotedNames())
                .configure(new LinterProviderTableWithSingleColumn())
                .configure(new LinterProviderTooManyLobs());
    }

}
