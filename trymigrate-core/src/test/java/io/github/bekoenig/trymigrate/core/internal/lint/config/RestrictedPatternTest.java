package io.github.bekoenig.trymigrate.core.internal.lint.config;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import schemacrawler.inclusionrule.RegularExpressionRule;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class RestrictedPatternTest {

    /**
     * This test asserts the side effects between {@link RestrictedPattern} and {@link RegularExpressionRule}.
     */
    @ParameterizedTest
    @CsvSource({
            // no additional patterns
            "SCHEMA_A.INTERNAL_DUMMY_ID,,,true",
            "SCHEMA_Z.INTERNAL_DUMMY_ID,,,false",
            "SCHEMA_A.migration_history,,,false",

            // empty additional patterns
            "SCHEMA_A.INTERNAL_DUMMY_ID,,'',true",
            "SCHEMA_A.INTERNAL_DUMMY_ID,'',,false",
            "SCHEMA_A.INTERNAL_DUMMY_ID,'','',false",

            // additional patterns for includes by single extension
            "SCHEMA_A.INTERNAL_DUMMY_ID,.*_ID,,true",
            "SCHEMA_A.INTERNAL_DUMMY_ID_TYPE,.*_ID,,false",
            "SCHEMA_A.INTERNAL_DUMMY,.*_ID,,false",
            "SCHEMA_A.INTERNAL_DUMMY_TXT,.*_ID,,false",
            "SCHEMA_A.INTERNAL_DUMMY_ID,.*_ID,'.*INTERNAL.*',false",
            "SCHEMA_A.internal_DUMMY_ID,.*_ID,'.*INTERNAL.*',true",
            "SCHEMA_Z.INTERNAL_DUMMY_ID,.*_ID,,false",
            "SCHEMA_A.migration_history,.*_ID,,false",

            // additional patterns for excludes
            "SCHEMA_A.INTERNAL_DUMMY_ID,,.*DUMMY,true",
            "SCHEMA_A.INTERNAL_DUMMY_ID,,.*DUMMY.*,false",

            // default include cannot be escaped
            "SCHEMA_Z.INTERNAL_DUMMY_ID,SCHEMA_Z.INTERNAL_DUMMY_ID,,false",
            // default exclude cannot be escaped
            "SCHEMA_A.migration_history,.*migration_history,,false",
    })
    void test(String tableFullName, String otherTableIncludePattern, String otherTableExcludePattern, boolean matches) {
        // GIVEN
        List<String> schemas = List.of("SCHEMA_A", "SCHEMA_B");
        RestrictedPattern restrictedPattern = new RestrictedPattern(
                "(.*\\.)?(" + String.join("|", schemas) + ")\\..*",
                "(.*\\.)?SCHEMA_A\\.migration_history");

        // WHEN
        RegularExpressionRule regularExpressionRule = new RegularExpressionRule(
                restrictedPattern.overlayIncludePattern(otherTableIncludePattern),
                restrictedPattern.overlayExcludePattern(otherTableExcludePattern));
        boolean test = regularExpressionRule.test(tableFullName);

        // THEN
        assertThat(test).isEqualTo(matches);
    }

}