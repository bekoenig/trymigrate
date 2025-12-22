package io.github.bekoenig.trymigrate.core.internal.lifecycle;

import io.github.bekoenig.trymigrate.core.TrymigrateWhenTarget;
import org.flywaydb.core.api.MigrationVersion;
import org.junit.jupiter.api.MethodDescriptor;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.MethodOrdererContext;
import org.junit.jupiter.api.Order;

import java.util.Comparator;
import java.util.Optional;


public class MigrateOrderer implements MethodOrderer {

    static final Comparator<MethodDescriptor> COMPARATOR = new Comparator<>() {

        @Override
        public int compare(MethodDescriptor m1, MethodDescriptor m2) {
            Optional<MigrationVersion> version1 = getMigrationVersion(m1);
            Optional<MigrationVersion> version2 = getMigrationVersion(m2);

            // target before no target
            if (Boolean.logicalXor(version1.isPresent(), version2.isPresent())) {
                return version1.isEmpty() ? 1 : -1;
            }

            // targets ascending version
            if (version1.isPresent() && !version1.get().equals(version2.get())) {
                return version1.get().compareTo(version2.get());
            }

            // same target or without target ascending order
            return Integer.compare(getOrder(m1), getOrder(m2));
        }

        private Optional<MigrationVersion> getMigrationVersion(MethodDescriptor m) {
            return m.findAnnotation(TrymigrateWhenTarget.class)
                    .map(TrymigrateWhenTarget::value)
                    .map(MigrationVersion::fromVersion);
        }

        private Integer getOrder(MethodDescriptor m) {
            return m.findAnnotation(Order.class)
                    .map(Order::value)
                    .orElse(Order.DEFAULT);
        }

    };

    @Override
    public void orderMethods(MethodOrdererContext context) {
        context.getMethodDescriptors().sort(COMPARATOR);
    }

}
