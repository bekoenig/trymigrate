package io.github.bekoenig.trymigrate.core.internal.schemacrawler.lint.config;

import io.github.bekoenig.trymigrate.core.lint.LintersConfiguration;
import schemacrawler.tools.linter.*;

import java.util.function.Consumer;

public class DefaultLinters implements Consumer<LintersConfiguration> {

    @Override
    public void accept(LintersConfiguration configuration) {
        configuration
                .register(new LinterProviderCatalogSql())
                .addConfig(new LinterProviderColumnTypes())
                .addConfig(new LinterProviderForeignKeyMismatch())
                .addConfig(new LinterProviderForeignKeySelfReference())
                .addConfig(new LinterProviderForeignKeyWithNoIndexes())
                .addConfig(new LinterProviderNullColumnsInIndex())
                .addConfig(new LinterProviderNullIntendedColumns())
                .addConfig(new LinterProviderRedundantIndexes())
                .addConfig(new LinterProviderTableAllNullableColumns())
                .addConfig(new LinterProviderTableCycles())
                .addConfig(new LinterProviderTableEmpty())
                .register(new LinterProviderTableSql())
                .addConfig(new LinterProviderTableWithBadlyNamedColumns())
                .addConfig(new LinterProviderTableWithIncrementingColumns())
                .addConfig(new LinterProviderTableWithNoIndexes())
                .addConfig(new LinterProviderTableWithNoPrimaryKey())
                .addConfig(new LinterProviderTableWithNoRemarks())
                .addConfig(new LinterProviderTableWithNoSurrogatePrimaryKey())
                .addConfig(new LinterProviderTableWithPrimaryKeyNotFirst())
                .addConfig(new LinterProviderTableWithQuotedNames())
                .addConfig(new LinterProviderTableWithSingleColumn())
                .addConfig(new LinterProviderTooManyLobs());
    }

}
