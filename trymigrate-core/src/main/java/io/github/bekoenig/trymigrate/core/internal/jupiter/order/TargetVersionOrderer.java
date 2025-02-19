package io.github.bekoenig.trymigrate.core.internal.jupiter.order;

import io.github.bekoenig.trymigrate.core.TrymigrateTest;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.MethodOrdererContext;
import org.junit.jupiter.api.Order;

/**
 * {@link MethodOrderer} zur Sortierung der Test-Methoden anhand der Flyway-Version definiert durch
 * {@link TrymigrateTest#whenTarget()}. Tests zur selben Flyway-Version werden gemäß der Definitionsreihenfolge
 * oder durch {@link Order} sortiert. Tests ohne die Annotation {@link TrymigrateTest} werden zuletzt ausgeführt.
 */
public class TargetVersionOrderer implements MethodOrderer {

    @Override
    public void orderMethods(MethodOrdererContext context) {
        context.getMethodDescriptors().sort(new TargetVersionComparator());
    }

}
