package com.github.andremoniy;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TreeAutomatonCSWGTest2 {

    // "!" after state name means it is final!
    private static final List<Rule> RULES_TABLE = Arrays.asList(
            new Rule("0", "e", "e", "e", null, "b(A(x2,x1),D(x2,x1),x2)"),
            new Rule("b(A(x2,x1),D(x2,x1),x2)", "b", "e", "e", null, "A(x1,x2)", "D(x2,x1)", "x2"),
            new Rule("A(x1,x2)", "e", "e", "e", new String[]{"x2", "x1"}, "a(x1,x2)"),
            new Rule("a(x1,x2)", "a", "e", "e", null, "x1", "x2"),
            new Rule("A(x1,x2)", "e", "e", "e", new String[]{"x2", "x1"}, "b(A(x2,x1),D(x2,x1),x2)"),
            new Rule("D(x2,x1)", "e", "e", "e", new String[]{"x2", "x1"}, "d(x1,x1,x2)"),
            new Rule("d(x1,x1,x2)", "d", "e", "e", null, "x1", "x1", "x2"),
            new Rule("x1", "a", "e", "e", new String[]{"x1"}, "x!"),
            new Rule("x1", "b", "e", "e", new String[]{"x1"}, "x!"),
            new Rule("x2", "a", "e", "e", new String[]{"x2"}, "x!"),
            new Rule("x2", "b", "e", "e", new String[]{"x2"}, "x!")
    );

    @Test
    void shouldParseCorrectTree1() {
        // Given
        final String treeString = "b(b(a(a,b),d(a,a,b),a),d(b,b,a),b)";
        final Tree tree = TreeParser.parse(treeString);
        assertEquals(treeString, tree.toString());

        final TreeAutomaton treeAutomaton = new TreeAutomaton(RULES_TABLE, tree);

        // When
        final boolean result = treeAutomaton.parse();

        // Then
        assertTrue(result);
    }

    @Test
    void shouldNotParseIncorrectTree1() {
        // Given
        final String treeString = "b(b(a(b,a),d(a,a,b),a),d(a,b,a),b)";
        final Tree tree = TreeParser.parse(treeString);
        assertEquals(treeString, tree.toString());

        final TreeAutomaton treeAutomaton = new TreeAutomaton(RULES_TABLE, tree);

        // When
        final boolean result = treeAutomaton.parse();

        // Then
        assertFalse(result);
    }

    @Test
    void testDotGraphBuilder() {
        // Given

        // When
        final String buildDot = DotGraphBuilder.buildDot(RULES_TABLE);

        // Then
        System.out.println(buildDot);

    }
}
