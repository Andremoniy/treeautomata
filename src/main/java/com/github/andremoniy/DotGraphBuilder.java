package com.github.andremoniy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class DotGraphBuilder {

    private DotGraphBuilder() {
    }

    static String buildDot(final List<Rule> ruleList) {

        final Map<String, String> graphNodesWithLabels = new HashMap<>();
        graphNodesWithLabels.put("0", "0");

        final AtomicInteger nodeCounter = new AtomicInteger(1);

        final Set<String> endNodes = ruleList.stream()
                .map(rule -> Stream.of(rule.nextStates()).filter(state -> state.endsWith("!")))
                .flatMap(Function.identity())
                .collect(Collectors.toSet());
        endNodes.forEach(node -> {
            final int nodeId = nodeCounter.getAndIncrement();
            graphNodesWithLabels.put(node, "N_" + nodeId);
            graphNodesWithLabels.put(node.substring(0, node.length() - 1), "N_" + nodeId);
        });

        final Set<String> otherNodes = ruleList.stream()
                .map(rule -> Stream.of(rule.nextStates()).filter(state -> !state.endsWith("!")))
                .flatMap(Function.identity())
                .collect(Collectors.toSet());
        otherNodes.forEach(node -> graphNodesWithLabels.put(node, "N_" + nodeCounter.getAndIncrement()));

        final StringBuilder dot = new StringBuilder();
        dot.append("digraph tree_automata {\n");
        dot.append("graph [ dpi = 300 ]; \n");
        dot.append("rankdir=LR;\n");
        dot.append("size=\"8.5\"\n");
        endNodes.forEach(endNode -> dot.append(graphNodesWithLabels.get(endNode)).append(" [ shape = doublecircle, label = \"").append(endNode.endsWith("!") ? endNode.substring(0, endNode.length() - 1) : endNode).append("\" ];\n"));
        otherNodes.forEach(node -> dot.append(graphNodesWithLabels.get(node)).append(" [ shape = circle, label = \"").append(node).append("\" ];\n"));
        dot.append("\n");

        ruleList.forEach(rule -> Stream.of(rule.nextStates()).forEach(
                state -> dot.append(graphNodesWithLabels.get(rule.state())).append(" -> ").append(graphNodesWithLabels.get(state)).append(" [ label = \"").append(rule.input()).append(", ").append(rule.stackRead()).append("->").append(rule.stackWrite()).append("\" ];\n")
        ));

        dot.append("}");
        return dot.toString();
    }
}
