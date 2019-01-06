package com.github.andremoniy;

import java.util.*;
import java.util.stream.Collectors;

public class Node {

    private final static Deque<String> EMPTY_STACK = new ArrayDeque<>();
    private final static Deque<String> NON_EMPTY_STACK = new ArrayDeque<>();

    private final String label;
    private final List<Node> nodes;
    private final Deque<String> stack;

    Node(String label, List<Node> nodes) {
        this.nodes = Collections.unmodifiableList(nodes);
        if (label.contains("[")) {
            this.label = label.substring(0, label.indexOf("["));
            if (label.contains("[]")) {
                this.stack = EMPTY_STACK;
            } else if (label.contains("[..]")) {
                this.stack = NON_EMPTY_STACK;
            } else {
                this.stack = new ArrayDeque<>();
                stack.add(label.substring(label.indexOf("[") + 1, label.indexOf("..")));
            }
        } else {
            this.stack = null;
            this.label = label;
        }
    }

    List<Node> nodes() {
        return nodes;
    }

    String label() {
        return label;
    }

    Node node(int nodeId) {
        return nodes.get(nodeId);
    }

    Deque<String> stack() {
        return stack;
    }

    @Override
    public String toString() {
        return label + (stack != null ? stackToString(stack) : "") + (nodes.isEmpty() ? "" : "(" + nodes.stream().map(Objects::toString).collect(Collectors.joining(",")) + ")");
    }

    private String stackToString(final Deque<String> stack) {
        if (stack == EMPTY_STACK) {
            return "[]";
        }
        if (stack == NON_EMPTY_STACK) {
            return "[..]";
        }
        return "[" + String.join("", stack) + ".." + "]";
    }
}
