package io.github.bekoenig.trymigrate.core;

import org.junit.jupiter.api.Test;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Diese Annotation markiert eine Methode als Test zu einer Flyway-Migration.
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Test
public @interface TrymigrateTest {

    /**
     * Erwartetes Flyway-Target für die Durchführung des Tests.
     *
     * @return Target als Version
     */
    String whenTarget();

    /**
     * Schalter zur Aktivierung der Bereinigung durch Flyway-Clean vor dem Start der Migration.
     *
     * @return {@code true} sofern eine Bereinigung erfolgen soll (Default ist {@code false})
     */
    boolean cleanBefore() default false;

    /**
     * Definition von Daten die vor der Ausführung des Flyway-Targets eingespielt werden.
     * <p>
     * Dumps als Zip-Datei werden über {@link TrymigrateDatabase#loadDump(String)} geladen.
     * SQL-Dateien oder einfache SQLs werden per JDBC ausgeführt.
     *
     * @return Datei mit SQLs, Zip mit Dump, SQLs
     */
    String[] givenData() default {};

}
