package io.github.bekoenig.trymigrate.core.internal.flyway.callback;

import io.github.bekoenig.trymigrate.core.config.TrymigrateDataLoadHandle;
import org.flywaydb.core.api.MigrationVersion;
import org.flywaydb.core.api.callback.Callback;
import org.flywaydb.core.api.callback.Context;
import org.flywaydb.core.api.callback.Event;
import us.fatehi.utility.IOUtility;
import us.fatehi.utility.database.SqlScript;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class DataLoader implements Callback {

    private final MigrationVersion target;
    private final String[] data;
    private final List<TrymigrateDataLoadHandle> handles;
    private boolean applied;

    public DataLoader(MigrationVersion target, String[] data, List<TrymigrateDataLoadHandle> handles) {
        this.target = target;
        this.data = data;
        this.handles = handles;
    }

    @Override
    public boolean supports(Event event, Context context) {
        if (event == Event.AFTER_MIGRATE && !applied) {
            throw new IllegalStateException("Data '" + Arrays.toString(data) +
                    "' was not be proceeded because schema is above version " + target);
        }

        return event == Event.BEFORE_EACH_MIGRATE
                && context.getMigrationInfo().getVersion().equals(target);
    }

    @Override
    public boolean canHandleInTransaction(Event event, Context context) {
        return true;
    }

    @Override
    public void handle(Event event, Context context) {
        for (String currentData : data) {

            String fileExtension = IOUtility.getFileExtension(currentData);

            List<TrymigrateDataLoadHandle> list = handles.stream().filter(h -> h.supports(currentData, fileExtension)).toList();
            if (list.size() > 1) {
                throw new UnsupportedOperationException("Expected single handle to load " + currentData +
                        " but found: \n" + list.stream().map(x -> x.getClass().getName()).collect(Collectors.joining("\n")));
            } else if (list.size() == 1) {
                list.get(0).handle(currentData);
                applied = true;
            }

            if (!applied) {
                if (fileExtension.equalsIgnoreCase("sql")) {
                    SqlScript.executeScriptFromResource(currentData, context.getConnection());
                } else {
                    try (Statement statement = context.getConnection().createStatement()) {
                        statement.execute(currentData);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
                applied = true;
            }
        }
    }

    @Override
    public String getCallbackName() {
        return getClass().getSimpleName();
    }

}
