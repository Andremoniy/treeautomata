package com.github.andremoniy;

import java.util.*;
import java.util.stream.Collectors;

public class Node {

    final static Deque<String> EMPTY_STACK = new ArrayDeque<>();
    final static Deque<String> NON_EMPTY_STACK = new ArrayDeque<>();

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return Objects.equals(label, node.label) &&
                Objects.equals(nodes, node.nodes) &&
                compareStacks(stack, node.stack);
    }

    // https://stackoverflow.com/questions/18203855/why-doesnt-arraydeque-override-equals-and-hashcode
    private boolean compareStacks(final Deque<String> stack1, final Deque<String> stack2) {
        if (stack1 == null && stack2 == null) {
            return true;
        }

        if (stack1 == null || stack2 == null) {
            return false;
        }

        return new ArrayList<>(stack1).equals(new ArrayList<>(stack2));
    }

    @Override
    public int hashCode() {
        return Objects.hash(label, nodes, stack);
    }
}
