package com.github.andremoniy;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Node {

    private final String label;
    private final List<Node> nodes;

    public Node(String label, List<Node> nodes) {
        this.label = label;
        this.nodes = Collections.unmodifiableList(nodes);
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

    @Override
    public String toString() {
        return label + (nodes.isEmpty() ? "" : "(" + nodes.stream().map(Objects::toString).collect(Collectors.joining(",")) + ")");
    }
}
