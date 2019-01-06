package com.github.andremoniy;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GrammarToRulesConverterTest {

    @Test
    void shouldConvertGrammarToRules1() {
        // Given
        final String grammar = "" +
                "S -> P[](b)" +
                "P[..](x1) —> P[a..](s(b,x1))" +
                "P[..](x1) —> K[..](s(a,x1))" +
                "K[a..](x1) —> s(K[..](x1), x1))" +
                "K[](x1) —> x1";

        final List<Rule> expectedRules = Arrays.asList(
                new Rule("0", "e", "e", "$", "x1"),
                new Rule("x1", "e", "e", "e", "K[](x1)"),
                new Rule("K[](x1)", "e", "e", "e", "K[..](x1)"),
                new Rule("K[..](x1)", "s", "e", "a", "K[..](x1)", "K[a..](x1)"),
                new Rule("K[a..](x1)", "e", "e", "e", "K[..](s(a,x1))"),
                new Rule("K[..](x1)", "e", "e", "e", "K[..](s(a,x1))"),
                new Rule("K[..](s(a,x1))", "s", "e", "e", "a_R", "P[..](x1)"),
                new Rule("a_R", "a", "e", "e", "a!"),
                new Rule("P[..](x1)", "e", "a", "e", "P[a..](s(b,x1))"),
                new Rule("P[..](x1)", "e", "$", "e", "P[](b)"),
                new Rule("P[](b)", "b", "e", "e", "S!"),
                new Rule("P[a..](s(b,x1))", "s", "e", "e", "s_B", "P[..](x1)"),
                new Rule("s_B", "b", "e", "e", "b!")
        );

        // When
        final List<Rule> rules = GrammarToRulesConverter.convert(grammar);

        // Then
        assertEquals(expectedRules.size(), rules.size());
        assertTrue(expectedRules.containsAll(rules));
    }

}
