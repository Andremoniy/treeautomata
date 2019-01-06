package com.github.andremoniy;

import java.util.*;
import java.util.stream.Collectors;

public class Node {

    private final String label;
    private final List<Node> nodes;
    private final Deque<String> stack;

    public Node(String label, List<Node> nodes) {
        this.nodes = Collections.unmodifiableList(nodes);
        if (label.contains("[")) {
            this.stack = new ArrayDeque<>();
            this.label = label.substring(0, label.indexOf("["));
            if (!label.contains("[..]")) {
                stack.add(label.substring(label.indexOf("[") + 1, label.indexOf("..")));
            }
        } else {
            this.stack = null;
            this.label = label;
        }
    }

    public List<Node> nodes() {
        return nodes;
    }

    public String label() {
        return label;
    }

    public Node node(int nodeId) {
        return nodes.get(nodeId);
    }

    public Deque<String> stack() {
        return stack;
    }

    @Override
    public String toString() {
        return label + (stack != null ? stackToString(stack) : "") + (nodes.isEmpty() ? "" : "(" + nodes.stream().map(Objects::toString).collect(Collectors.joining(",")) + ")");
    }

    private String stackToString(final Deque<String> stack) {
        return "[" + String.join("", stack) + ".." + "]";
    }
}
