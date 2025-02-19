package io.github.bekoenig.trymigrate.database.postgresql;

import schemacrawler.schema.Table;
import schemacrawler.tools.lint.*;
import us.fatehi.utility.property.PropertyName;

import java.sql.Connection;

public class DummyLinterProvider extends BaseLinterProvider {

    public DummyLinterProvider() {
        super(DummyLinter.class.getName());
    }

    @Override
    public Linter newLinter(final LintCollector lintCollector) {
        return new DummyLinter(getPropertyName(), lintCollector);
    }
}

class DummyLinter extends BaseLinter {

    DummyLinter(final PropertyName propertyName, final LintCollector lintCollector) {
        super(propertyName, lintCollector);
        setSeverity(LintSeverity.low);
    }

    @Override
    public String getSummary() {
        return "dummy lint";
    }

    @Override
    protected void lint(final Table table, final Connection connection) {
        addTableLint(table, getSummary());
    }

}

