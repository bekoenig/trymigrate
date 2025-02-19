package io.github.bekoenig.trymigrate.core.internal.jupiter.order;

import io.github.bekoenig.trymigrate.core.TrymigrateTest;
import org.flywaydb.core.api.MigrationVersion;
import org.junit.jupiter.api.MethodDescriptor;
import org.junit.jupiter.api.Order;

import java.util.Comparator;
import java.util.Optional;

public class TargetVersionComparator implements Comparator<MethodDescriptor> {

    @Override
    public int compare(MethodDescriptor m1, MethodDescriptor m2) {
        Optional<MigrationVersion> version1 = getMigrationVersion(m1);
        Optional<MigrationVersion> version2 = getMigrationVersion(m2);

        // Tests mit Target werden vor denen ohne Target einsortiert.
        if (Boolean.logicalXor(version1.isPresent(), version2.isPresent())) {
            return version1.isEmpty() ? 1 : -1;
        }

        // Tests mit unterschiedlichem Target werden nach der Version sortiert.
        if (version1.isPresent() && !version1.get().equals(version2.get())) {
            return version1.get().compareTo(version2.get());
        }

        // Alle übrigen Tests werden nach der Order sortiert.
        return Integer.compare(getOrder(m1), getOrder(m2));
    }

    private Optional<MigrationVersion> getMigrationVersion(MethodDescriptor m) {
        return m.findAnnotation(TrymigrateTest.class).map(x -> MigrationVersion.fromVersion(x.whenTarget()));
    }

    private static Integer getOrder(MethodDescriptor m) {
        return m.findAnnotation(Order.class).map(Order::value).orElse(Order.DEFAULT);
    }

}
