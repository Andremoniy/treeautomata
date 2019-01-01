package com.github.andremoniy;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TreeAutomatonTest {

    // "!" after state name means it is final!
    private static final List<Rule> RULES_TABLE = Arrays.asList(
            new Rule("0", "e", "e", "$", "1"),
            new Rule("1", "e", "e", "e", "2"),
            new Rule("2", "s", "e", "e", "a_R", "4"),
            new Rule("a_R", "a", "e", "e", "a!"),
            new Rule("4", "e", "$", "e", "5"),
            new Rule("5", "b", "e", "e", "S!"),
            new Rule("4", "e", "e", "e", "6"),
            new Rule("6", "s", "a", "e", "b_R", "4"),
            new Rule("b_R", "b", "e", "e", "b!"),
            new Rule("1", "e", "e", "e", "3"),
            new Rule("3", "s", "e", "a", "1", "2")
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


}
