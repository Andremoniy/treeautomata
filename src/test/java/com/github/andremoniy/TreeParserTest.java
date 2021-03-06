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

    @Test
    void shouldParseTreeFromStringWithIndexes() {
        // Given
        final String treeString = "s(K[..](x1),x1)";

        // When
        final Tree tree = TreeParser.parse(treeString);

        // Then
        assertEquals(treeString, tree.toString());
    }

    @Test
    void shouldParseTreeFromStringWithIndexes2() {
        // Given
        final String treeString = "s(K[a..](x1),x1)";

        // When
        final Tree tree = TreeParser.parse(treeString);

        // Then
        assertEquals(treeString, tree.toString());
    }

    @Test
    void shouldParseTreeFromStringWithIndexes3() {
        // Given
        final String treeString = "K[a..](x1)";

        // When
        final Tree tree = TreeParser.parse(treeString);

        // Then
        assertEquals(treeString, tree.toString());
    }

    @Test
    void shouldParseSingleNodeTree() {
        // Given
        final String treeString = "S";

        // When
        final Tree tree = TreeParser.parse(treeString);

        // Then
        assertEquals(treeString, tree.toString());
    }

    @Test
    void shouldParseTreeWithEmptyIndex() {
        // Given
        final String treeString = "P[](b)";

        // When
        final Tree tree = TreeParser.parse(treeString);

        // Then
        assertEquals(treeString, tree.toString());
    }

}
