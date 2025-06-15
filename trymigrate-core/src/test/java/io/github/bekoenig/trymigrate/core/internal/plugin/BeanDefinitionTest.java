package io.github.bekoenig.trymigrate.core.internal.plugin;

import io.github.bekoenig.trymigrate.core.plugin.TrymigrateBean;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SuppressWarnings("unused")
class BeanDefinitionTest {

    private static BeanDefinition getBeanDefinition(Object instance) {
        return getBeanDefinition(instance, 0);
    }

    private static BeanDefinition getBeanDefinition(Object instance, Integer hierarchy) {
        Field field;
        try {
            field = instance.getClass().getDeclaredField("attribute");
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
        field.setAccessible(true);
        return new BeanDefinition(instance, field, hierarchy);
    }

    @Test
    void compareTo_same() {
        // GIVEN
        BeanDefinition beanDefinition1 = getBeanDefinition(new Object() {
            private final String attribute = null;
        });
        BeanDefinition beanDefinition2 = getBeanDefinition(new Object() {
            private final String attribute = null;
        });

        // WHEN
        int compareTo = beanDefinition1.compareTo(beanDefinition2);

        // THEN
        assertThat(compareTo).isEqualTo(0);
    }

    @Test
    void compareTo_differentOrder() {
        // GIVEN
        BeanDefinition beanDefinition1 = getBeanDefinition(new Object() {
            @Order(Integer.MIN_VALUE)
            private final String attribute = null;
        });
        BeanDefinition beanDefinition2 = getBeanDefinition(new Object() {
            @Order(Integer.MAX_VALUE)
            private final String attribute = null;
        });

        // WHEN
        int compareTo = beanDefinition1.compareTo(beanDefinition2);

        // THEN
        assertThat(compareTo).isLessThan(0);
    }

    @Test
    void compareTo_differentHierarchy() {
        // GIVEN
        BeanDefinition beanDefinition1 = getBeanDefinition(new Object() {
            private final String attribute = null;
        }, 2);
        BeanDefinition beanDefinition2 = getBeanDefinition(new Object() {
            private final String attribute = null;
        }, 0);

        // WHEN
        int compareTo = beanDefinition1.compareTo(beanDefinition2);

        // THEN
        assertThat(compareTo).isLessThan(0);
    }

    @Test
    void compareTo_differentNullable() {
        // GIVEN
        BeanDefinition beanDefinition1 = getBeanDefinition(new Object() {
            @TrymigrateBean(nullable = true)
            private final String attribute = null;
        });
        BeanDefinition beanDefinition2 = getBeanDefinition(new Object() {
            private final String attribute = null;
        });

        // WHEN
        int compareTo = beanDefinition1.compareTo(beanDefinition2);

        // THEN
        assertThat(compareTo).isLessThan(0);
    }

    @Test
    void nonNullable_isTrue() {
        // GIVEN
        BeanDefinition beanDefinition = getBeanDefinition(new Object() {
            private final String attribute = null;
        });

        // WHEN
        boolean result = beanDefinition.nonNullable();

        // THEN
        assertThat(result).isTrue();
    }

    @Test
    void nonNullable_isFalse() {
        // GIVEN
        BeanDefinition beanDefinition = getBeanDefinition(new Object() {
            @TrymigrateBean(nullable = true)
            private final String attribute = null;
        });

        // WHEN
        boolean result = beanDefinition.nonNullable();

        // THEN
        assertThat(result).isFalse();
    }

    @Test
    void isCompatible_isTrue() {
        // GIVEN
        BeanDefinition beanDefinition = getBeanDefinition(new Object() {
            private final String attribute = "a";
        });

        // WHEN
        boolean result = beanDefinition.isCompatible(String.class);

        // THEN
        assertThat(result).isTrue();
    }

    @Test
    void isCompatible_isFalseOnDifferentSimpleType() {
        // GIVEN
        BeanDefinition beanDefinition = getBeanDefinition(new Object() {
            private final String attribute = "a";
        });

        // WHEN
        boolean result = beanDefinition.isCompatible(Integer.class);

        // THEN
        assertThat(result).isFalse();
    }

    @Test
    void isCompatible_isFalseOnDifferentListType() {
        // GIVEN
        BeanDefinition beanDefinition = getBeanDefinition(new Object() {
            private final List<String> attribute = List.of("a");
        });

        // WHEN
        boolean result = beanDefinition.isCompatible(Integer.class);

        // THEN
        assertThat(result).isFalse();
    }

    @Test
    void isCompatible_isTrueOnSameType() {
        // GIVEN
        BeanDefinition beanDefinition = getBeanDefinition(new Object() {
            private final List<String> attribute = List.of("a");
        });

        // WHEN
        boolean result = beanDefinition.isCompatible(String.class);

        // THEN
        assertThat(result).isTrue();
    }

    @Test
    void isCompatible_isTrueOnCompatibleType() {
        // GIVEN
        BeanDefinition beanDefinition = getBeanDefinition(new Object() {
            private final List<String> attribute = List.of("a");
        });

        // WHEN
        boolean result = beanDefinition.isCompatible(Object.class);

        // THEN
        assertThat(result).isTrue();
    }

    @Test
    void isCompatible_isTrueOnCompatibleGenericType() {
        // GIVEN
        BeanDefinition beanDefinition = getBeanDefinition(new Object() {
            private final List<List<Object>> attribute = List.of(List.of("a"));
        });

        // WHEN
        boolean result = beanDefinition.isCompatible(Collection.class);

        // THEN
        assertThat(result).isTrue();
    }

    @Test
    @SuppressWarnings("rawtypes")
    void isCompatible_throwsOnNoGeneric() {
        // GIVEN
        BeanDefinition beanDefinition = getBeanDefinition(new Object() {
            private final List attribute = List.of("a");
        });

        // WHEN & THEN
        assertThatThrownBy(() -> beanDefinition.isCompatible(String.class));
    }

    @Test
    void isCompatible_throwsOnManyGenerics() {
        // GIVEN
        interface BiList<A, B> extends List<A> {
        }

        class BiArrayList<A, B> extends ArrayList<A> implements BiList<A, B> {
            public BiArrayList(A a) {
                this.add(a);
            }
        }

        BeanDefinition beanDefinition = getBeanDefinition(new Object() {
            private final BiList<String, String> attribute = new BiArrayList<>("a");
        });

        // WHEN & THEN
        assertThatThrownBy(() -> beanDefinition.isCompatible(String.class));
    }

    @Test
    void isNullable_isTrue() {
        // GIVEN
        BeanDefinition beanDefinition = getBeanDefinition(new Object() {
            @TrymigrateBean(nullable = true)
            private final String attribute = "a";
        });

        // WHEN
        boolean result = beanDefinition.isNullable();

        // THEN
        assertThat(result).isTrue();
    }

    @Test
    void isNullable_isFalse() {
        // GIVEN
        BeanDefinition beanDefinition = getBeanDefinition(new Object() {
            private final String attribute = "a";
        });

        // WHEN
        boolean result = beanDefinition.isNullable();

        // THEN
        assertThat(result).isFalse();
    }

    @Test
    void isNullable_isFalseOnAnnotation() {
        // GIVEN
        BeanDefinition beanDefinition = getBeanDefinition(new Object() {
            @TrymigrateBean
            private final String attribute = "a";
        });

        // WHEN
        boolean result = beanDefinition.isNullable();

        // THEN
        assertThat(result).isFalse();
    }

    @Test
    void get_simpleType() {
        // GIVEN
        BeanDefinition beanDefinition = getBeanDefinition(new Object() {
            private final String attribute = "a";
        });

        // WHEN
        Collection<String> attribute = beanDefinition.get();

        // THEN
        assertThat(attribute).containsExactly("a");
    }

    @Test
    void get_nullOnNullable() {
        // GIVEN
        BeanDefinition beanDefinition = getBeanDefinition(new Object() {
            @TrymigrateBean(nullable = true)
            private String attribute;
        });

        // WHEN
        Collection<String> attribute = beanDefinition.get();

        // THEN
        assertThat(attribute).isEmpty();
    }

    @Test
    void get_throwsOnIncompatibleSimpleType() {
        // GIVEN
        BeanDefinition beanDefinition = getBeanDefinition(new Object() {
            private String attribute;
        });

        // WHEN & THEN
        assertThatThrownBy(beanDefinition::get);
    }

    @Test
    void get_throwsOnNonNullableSimpleType() {
        // GIVEN
        BeanDefinition beanDefinition = getBeanDefinition(new Object() {
            private String attribute;
        });

        // WHEN & THEN
        assertThatThrownBy(beanDefinition::get);
    }

    @Test
    void get_listType() {
        // GIVEN
        BeanDefinition beanDefinition = getBeanDefinition(new Object() {
            private final List<String> attribute = List.of("a");
        });

        // WHEN
        Collection<String> collection = beanDefinition.get();

        // THEN
        assertThat(collection).containsExactly("a");
    }

    @Test
    void get_wrapSingle() {
        // GIVEN
        BeanDefinition beanDefinition = getBeanDefinition(new Object() {
            private final String attribute = "a";
        });

        // WHEN
        Collection<String> collection = beanDefinition.get();

        // THEN
        assertThat(collection).containsExactly("a");
    }

    @Test
    void get_emptyOnNullable() {
        // GIVEN
        BeanDefinition beanDefinition = getBeanDefinition(new Object() {
            @TrymigrateBean(nullable = true)
            private final String attribute = null;
        });

        // WHEN
        Collection<String> collection = beanDefinition.get();

        // THEN
        assertThat(collection).isEmpty();
    }

    void getOrder_valueOnAnnotation() {
        // GIVEN
        BeanDefinition beanDefinition = getBeanDefinition(new Object() {
            @Order(5)
            private String attribute;
        });

        // WHEN
        Integer order = beanDefinition.getOrder();

        // THEN
        assertThat(order).isEqualTo(5);
    }

    void getOrder_defaultOnNoAnnotation() {
        // GIVEN
        BeanDefinition beanDefinition = getBeanDefinition(new Object() {
            private String attribute;
        });

        // WHEN
        Integer order = beanDefinition.getOrder();

        // THEN
        assertThat(order).isEqualTo(Order.DEFAULT);
    }

}