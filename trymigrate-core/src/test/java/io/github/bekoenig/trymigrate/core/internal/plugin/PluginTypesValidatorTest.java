package io.github.bekoenig.trymigrate.core.internal.plugin;

import cr.Classpath;
import org.flywaydb.core.api.migration.JavaMigration;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import static org.assertj.core.api.Assertions.assertThat;

class PluginTypesValidatorTest {

    @Test
    void isSupportedType_trueOnSubclass() {
        // GIVEN
        Class<?> clazz = MyJavaMigration.class;

        // WHEN
        boolean actual = PluginTypesValidator.isSupportedType(clazz);

        // THEN
        assertThat(actual).isTrue();
    }

    @Test
    void isSupportedType_falseOnSuperclass() {
        // GIVEN
        Class<?> clazz = GenericContainer.class;

        // WHEN
        boolean actual = PluginTypesValidator.isSupportedType(clazz);

        // THEN
        assertThat(actual).isFalse();
    }

    @Test
    @Classpath(exclude = "org.testcontainers:testcontainers-jdbc")
    void isSupportedType_false() {
        // GIVEN
        Class<?> clazz = String.class;

        // WHEN
        boolean actual = PluginTypesValidator.isSupportedType(clazz);

        // THEN
        assertThat(actual).isFalse();
    }

    abstract static class MyJavaMigration implements JavaMigration {
    }

}