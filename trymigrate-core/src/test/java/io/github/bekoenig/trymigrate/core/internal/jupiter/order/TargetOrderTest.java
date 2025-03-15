package io.github.bekoenig.trymigrate.core.internal.jupiter.order;

import io.github.bekoenig.trymigrate.core.TrymigrateTest;
import org.junit.jupiter.api.MethodDescriptor;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TargetOrderTest {

    private final TargetOrder bean = new TargetOrder();

    @Test
    void compare_same() {
        // GIVEN
        MethodDescriptor md1 = mock();
        MethodDescriptor md2 = mock();

        // WHEN
        int result = bean.compare(md1, md2);

        // THEN
        assertThat(result).isZero();
    }

    @Test
    void compare_differentOrder() {
        // GIVEN
        MethodDescriptor md1 = mock();
        Order order1 = mock();
        when(order1.value()).thenReturn(0);
        when(md1.findAnnotation(eq(Order.class))).thenReturn(Optional.of(order1));

        MethodDescriptor md2 = mock();
        Order order2 = mock();
        when(order2.value()).thenReturn(1);
        when(md2.findAnnotation(eq(Order.class))).thenReturn(Optional.of(order2));

        // WHEN
        int result = bean.compare(md1, md2);

        // THEN
        assertThat(result).isLessThan(0);
    }

    @Test
    void compare_targetBeforeNonTarget1() {
        // GIVEN
        MethodDescriptor md1 = mock();
        when(md1.findAnnotation(eq(TrymigrateTest.class)))
                .thenReturn(Optional.ofNullable(mock()));

        MethodDescriptor md2 = mock();

        // WHEN
        int result = bean.compare(md1, md2);

        // THEN
        assertThat(result).isLessThan(0);
    }

    @Test
    void compare_targetBeforeNonTarget2() {
        // GIVEN
        MethodDescriptor md1 = mock();

        MethodDescriptor md2 = mock();
        when(md2.findAnnotation(eq(TrymigrateTest.class)))
                .thenReturn(Optional.ofNullable(mock()));

        // WHEN
        int result = bean.compare(md1, md2);

        // THEN
        assertThat(result).isGreaterThan(0);
    }

    @Test
    void compare_differentTarget() {
        // GIVEN
        MethodDescriptor md1 = mock();
        TrymigrateTest trymigrateTest1 = mock();
        when(trymigrateTest1.whenTarget()).thenReturn("1.0");
        when(md1.findAnnotation(eq(TrymigrateTest.class))).thenReturn(Optional.of(trymigrateTest1));

        MethodDescriptor md2 = mock();
        TrymigrateTest trymigrateTest2 = mock();
        when(trymigrateTest2.whenTarget()).thenReturn("1.1");
        when(md2.findAnnotation(eq(TrymigrateTest.class))).thenReturn(Optional.of(trymigrateTest2));

        // WHEN
        int result = bean.compare(md1, md2);

        // THEN
        assertThat(result).isLessThan(0);
    }

    @Test
    void compare_sameTargetDifferentOrder() {
        // GIVEN
        MethodDescriptor md1 = mock();
        TrymigrateTest trymigrateTest1 = mock();
        when(trymigrateTest1.whenTarget()).thenReturn("1.0");
        when(md1.findAnnotation(eq(TrymigrateTest.class))).thenReturn(Optional.of(trymigrateTest1));
        Order order1 = mock();
        when(order1.value()).thenReturn(0);
        when(md1.findAnnotation(eq(Order.class))).thenReturn(Optional.of(order1));

        MethodDescriptor md2 = mock();
        TrymigrateTest trymigrateTest2 = mock();
        when(trymigrateTest2.whenTarget()).thenReturn("1.0");
        when(md2.findAnnotation(eq(TrymigrateTest.class))).thenReturn(Optional.of(trymigrateTest2));
        Order order2 = mock();
        when(order2.value()).thenReturn(1);
        when(md2.findAnnotation(eq(Order.class))).thenReturn(Optional.of(order2));

        // WHEN
        int result = bean.compare(md1, md2);

        // THEN
        assertThat(result).isLessThan(0);
    }
}