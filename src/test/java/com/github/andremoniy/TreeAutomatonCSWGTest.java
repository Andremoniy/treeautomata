package com.github.andremoniy;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TreeAutomatonCSWGTest {

    // "!" after state name means it is final!
    private static final List<Rule> RULES_TABLE = Arrays.asList(
            new Rule("0", "e", "e", "$", null, "x1"),
            new Rule("x1", "e", "e", "e", null, "K[..](x1)"),
            new Rule("K[..](x1)", "e", "e", "e", null, "s(K[..](x1),x1)"),
            new Rule("s(K[..](x1),x1)", "s", "e", "a", null, "K[..](x1)", "x1_2"),
            new Rule("x1_2", "e", "e", "e", new String[]{"x1"}, "s(a,x1)"),
            new Rule("s(a,x1)", "s", "e", "e", null, "a_R", "x1_3"),
            new Rule("a_R", "a", "e", "e", null, "a!"),
            new Rule("x1_3", "e", "e", "e", null, "s(b,x1)"),
            new Rule("s(b,x1)", "s", "e", "e", null, "b_R", "x1_3"),
            new Rule("x1_3", "b", "e", "e", null, "b!"),
            new Rule("K[..](x1)", "e", "e", "e", new String[]{"x1"}, "P[a..](s(a,x1))"),
            new Rule("P[a..](s(a,x1))", "s", "e", "e", null, "a_R", "P[..](x1)"),
            new Rule("P[..](x1)", "e", "e", "e", null, "P[..](s(b,x1))"),
            new Rule("P[..](s(b,x1))", "s", "a", "e", null, "b_R", "P[..](x1)"),
            new Rule("b_R", "b", "e", "e", null, "b!"),
            new Rule("P[..](x1)", "e", "e", "e", null, "P[](b)"),
            new Rule("P[](b)", "b", "$", "e", null, "S!")
    );

    @Test
    void shouldParseCorrectTree1() {
        // Given
        final String treeString = "s(a,b)";
        final Tree tree = TreeParser.parse(treeString);

        final TreeAutomaton treeAutomaton = new TreeAutomaton(RULES_TABLE, tree);

        // When
        final boolean result = treeAutomaton.parse();

        // Then
        assertTrue(result);
    }

    @Test
    void shouldNotParseIncorrectTree1() {
        // Given
        final String treeString = "s(a,b,c)";
        final Tree tree = TreeParser.parse(treeString);

        final TreeAutomaton treeAutomaton = new TreeAutomaton(RULES_TABLE, tree);

        // When
        final boolean result = treeAutomaton.parse();

        // Then
        assertFalse(result);
    }

    @Test
    void shouldParseCorrectTree2() {
        // Given
        final String treeString = "s(s(a,s(b,b)),s(a,s(b,b)))";
        final Tree tree = TreeParser.parse(treeString);

        final TreeAutomaton treeAutomaton = new TreeAutomaton(RULES_TABLE, tree);

        // When
        final boolean result = treeAutomaton.parse();

        // Then
        assertTrue(result);
    }

    @Test
    void shouldParseCorrectTree3() {
        // Given
        final String treeString = "s(s(s(a,s(b,s(b,b))),s(a,s(b,s(b,b)))),s(a,s(b,s(b,b))))";
        final Tree tree = TreeParser.parse(treeString);

        final TreeAutomaton treeAutomaton = new TreeAutomaton(RULES_TABLE, tree);

        // When
        final boolean result = treeAutomaton.parse();

        // Then
        assertTrue(result);
    }

    @Test
    void shouldNotParseCorrectTree4() {
        // Given
        final String treeString = "s(s(s(a,s(b,s(b,s(b,b)))),s(a,s(b,s(b,b)))),s(a,s(b,s(b,b))))";
        final Tree tree = TreeParser.parse(treeString);

        final TreeAutomaton treeAutomaton = new TreeAutomaton(RULES_TABLE, tree);

        // When
        final boolean result = treeAutomaton.parse();

        // Then
        assertFalse(result);
    }

    @Test
    void shouldNotParseCorrectTree5() {
        // Given
        final String treeString = "s(s(s(a,s(b,s(b,b))),s(a,s(b,s(b,b)))),s(s(a,s(b,s(b,b))),s(a,s(b,s(b,b)))))";
        final Tree tree = TreeParser.parse(treeString);

        final TreeAutomaton treeAutomaton = new TreeAutomaton(RULES_TABLE, tree);

        // When
        final boolean result = treeAutomaton.parse();

        // Then
        assertFalse(result);
    }


    @Test
    void shouldNotParseCorrectTree6() {
        // Given
        final String treeString = "s(s(s(a,s(b,s(b,s(b,b)))),s(a,s(b,s(b,b)))),s(a,s(b,s(b,s(b,b)))))";
        final Tree tree = TreeParser.parse(treeString);

        final TreeAutomaton treeAutomaton = new TreeAutomaton(RULES_TABLE, tree);

        // When
        final boolean result = treeAutomaton.parse();

        // Then
        assertFalse(result);
    }

}
