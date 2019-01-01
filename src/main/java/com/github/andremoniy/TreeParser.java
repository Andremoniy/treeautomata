package com.github.andremoniy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TreeParser {

    public static Tree parse(String treeString) {
        treeString = treeString.trim();
        int leftParenthisIndex = treeString.indexOf('(');
        final String label = treeString.substring(0, leftParenthisIndex > 0 ? leftParenthisIndex : treeString.length()).trim();
        final List<Node> nodes = leftParenthisIndex > 0
                ? parseNodes(treeString.substring(leftParenthisIndex + 1, treeString.lastIndexOf(')')).trim())
                : Collections.emptyList();

        return new Tree(label, nodes);
    }

    private static List<Node> parseNodes(String treeList) {
        final List<Node> nodes = new ArrayList<>();
        final StringBuilder treeString = new StringBuilder();
        int parenthisNumber = 0;
        for (char c : treeList.toCharArray()) {
            if (c == '(') {
                parenthisNumber++;
            }
            if (c == ')') {
                parenthisNumber--;
            }
            if (c == ',') {
                if (parenthisNumber == 0) {
                    nodes.add(parse(treeString.toString()));
                    treeString.setLength(0);
                } else {
                    treeString.append(c);
                }
            } else {
                treeString.append(c);
            }
        }
        if (treeString.length() > 0) {
            nodes.add(parse(treeString.toString()));
        }
        return nodes;
    }
}
