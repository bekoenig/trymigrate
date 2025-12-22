package io.github.bekoenig.trymigrate.core.internal.lifecycle;

import io.github.bekoenig.trymigrate.core.TrymigrateWhenTarget;
import org.junit.jupiter.api.MethodDescriptor;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MigrateOrdererTest {

    @Test
    void compare_same() {
        // GIVEN
        MethodDescriptor md1 = mock();
        MethodDescriptor md2 = mock();

        // WHEN
        int result = MigrateOrderer.COMPARATOR.compare(md1, md2);

        // THEN
        assertThat(result).isZero();
    }

    @Test
    void compare_differentOrder() {
        // GIVEN
        MethodDescriptor md1 = mock();
        mockOrder(0, md1);

        MethodDescriptor md2 = mock();
        mockOrder(1, md2);

        // WHEN
        int result = MigrateOrderer.COMPARATOR.compare(md1, md2);

        // THEN
        assertThat(result).isLessThan(0);
    }

    @Test
    void compare_targetBeforeNonTarget1() {
        // GIVEN
        MethodDescriptor md1 = mock();
        mockWhenTarget(md1, "1.0");

        MethodDescriptor md2 = mock();

        // WHEN
        int result = MigrateOrderer.COMPARATOR.compare(md1, md2);

        // THEN
        assertThat(result).isLessThan(0);
    }

    @Test
    void compare_targetBeforeNonTarget2() {
        // GIVEN
        MethodDescriptor md1 = mock();

        MethodDescriptor md2 = mock();
        mockWhenTarget(md2, "1.0");

        // WHEN
        int result = MigrateOrderer.COMPARATOR.compare(md1, md2);

        // THEN
        assertThat(result).isGreaterThan(0);
    }

    @Test
    void compare_differentTarget() {
        // GIVEN
        MethodDescriptor md1 = mock();
        mockWhenTarget(md1, "1.0");

        MethodDescriptor md2 = mock();
        mockWhenTarget(md2, "1.1");

        // WHEN
        int result = MigrateOrderer.COMPARATOR.compare(md1, md2);

        // THEN
        assertThat(result).isLessThan(0);
    }

    @Test
    void compare_sameTargetDifferentOrder() {
        // GIVEN
        MethodDescriptor md1 = mock();
        mockWhenTarget(md1, "1.0");
        mockOrder(0, md1);

        MethodDescriptor md2 = mock();
        mockWhenTarget(md2, "1.0");
        mockOrder(1, md2);

        // WHEN
        int result = MigrateOrderer.COMPARATOR.compare(md1, md2);

        // THEN
        assertThat(result).isLessThan(0);
    }

    private static void mockWhenTarget(MethodDescriptor md1, String target) {
        TrymigrateWhenTarget whenTarget = mock();
        when(whenTarget.value()).thenReturn(target);
        when(md1.findAnnotation(eq(TrymigrateWhenTarget.class))).thenReturn(Optional.of(whenTarget));
    }

    private static void mockOrder(int value, MethodDescriptor md2) {
        Order order2 = mock();
        when(order2.value()).thenReturn(value);
        when(md2.findAnnotation(eq(Order.class))).thenReturn(Optional.of(order2));
    }
}