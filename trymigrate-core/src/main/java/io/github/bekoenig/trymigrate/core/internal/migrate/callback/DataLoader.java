package io.github.bekoenig.trymigrate.core.internal.migrate.callback;

import io.github.bekoenig.trymigrate.core.plugin.customize.TrymigrateDataLoader;
import io.github.bekoenig.trymigrate.core.plugin.customize.TrymigrateDatabase;
import org.flywaydb.core.api.MigrationVersion;
import org.flywaydb.core.api.callback.Callback;
import org.flywaydb.core.api.callback.Context;
import org.flywaydb.core.api.callback.Event;
import us.fatehi.utility.IOUtility;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

public class DataLoader implements Callback {

    private final TrymigrateDatabase database;
    private final List<TrymigrateDataLoader> dataLoaders;
    private final MigrationVersion target;
    private final List<String> resources;

    private boolean applied;

    public DataLoader(TrymigrateDatabase database, List<TrymigrateDataLoader> dataLoaders, MigrationVersion target,
                      List<String> resources) {
        this.database = database;
        this.dataLoaders = dataLoaders;
        this.target = target;
        this.resources = resources;
    }

    @Override
    public boolean supports(Event event, Context context) {
        if (resources.isEmpty()) {
            return false;
        }

        if (event == Event.AFTER_MIGRATE && !applied) {
            throw new IllegalStateException("Data was not be proceeded because schema is above version " + target);
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
        resources.forEach(resource -> load(resource, context.getConnection()));
        applied = true;
    }

    private void load(String resource, Connection connection) {
        Optional<TrymigrateDataLoader> dataLoader = dataLoaders.stream()
                .filter(h -> h.supports(resource, IOUtility.getFileExtension(resource), database))
                .findFirst();

        if (dataLoader.isPresent()) {
            dataLoader.get().load(resource, connection, database);
        } else {
            try (Statement statement = connection.createStatement()) {
                statement.execute(resource);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public String getCallbackName() {
        return getClass().getSimpleName();
    }

}
