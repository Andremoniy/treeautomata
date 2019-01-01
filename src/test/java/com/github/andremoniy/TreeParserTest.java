package com.github.andremoniy;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TreeParserTest {

    @Test
    void shouldParseTreeFromString1() {
        // Given
        final String treeString = "s(a,b)";

        // When
        final Tree tree = TreeParser.parse(treeString);

        // Then
        assertEquals("s", tree.label());
        assertEquals(2, tree.nodes().size());
        assertEquals("a", tree.node(0).label());
        assertEquals(0, tree.node(0).nodes().size());
        assertEquals("b", tree.node(1).label());
        assertEquals(0, tree.node(1).nodes().size());
    }

    @Test
    void shouldParseTreeFromString2() {
        // Given
        final String treeString = "s(s(a,s(b,b)),s(a,s(b,b)))";

        // When
        final Tree tree = TreeParser.parse(treeString);

        // Then
        assertEquals(treeString, tree.toString());
    }

}
