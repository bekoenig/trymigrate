package io.github.bekoenig.trymigrate.database.postgresql;

import schemacrawler.schema.Table;
import schemacrawler.tools.lint.*;
import us.fatehi.utility.property.PropertyName;

import java.io.Serial;
import java.sql.Connection;

public class DummyLinterProvider implements LinterProvider {

    @Serial
    private static final long serialVersionUID = -6555525517116449604L;

    @Override
    public PropertyName getPropertyName() {
        return new PropertyName(DummyLinter.class.getName(), null);
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

