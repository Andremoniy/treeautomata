package com.github.andremoniy;

import java.util.*;
import java.util.stream.Collectors;

public class TreeAutomaton {

    private final int debugTab;
    private final List<Rule> rulesTable;
    private final Stack<int[]> accordanceStack;
    private final Stack<String> stack;
    private final String currentState;
    private final Map<String, Node> results = new HashMap<>();
    private final Node node;

    private TreeAutomaton(
            final int debugTab,
            final List<Rule> rulesTable,
            final Stack<int[]> accordanceStack,
            final Stack<String> stack,
            final String currentState,
            final Node node
    ) {
        this.debugTab = debugTab;
        this.rulesTable = rulesTable;
        this.accordanceStack = accordanceStack;
        this.stack = stack;
        this.currentState = currentState;
        this.node = node;
    }

    public TreeAutomaton(final List<Rule> rulesTable, final Node node) {
        this(0, rulesTable, new Stack<>(), new Stack<>(), "0", node);
    }

    public boolean parse() {

        final List<Rule> possibleRules = rulesTable.stream().filter(rule -> rule.state().equals(currentState)).collect(Collectors.toList());
        final List<Rule> applicableRules = possibleRules.stream().filter(this::tryRule).collect(Collectors.toList());

        for (Rule rule : applicableRules) {
            if (applyRule(rule)) {
                return true;
            }
        }

//        System.err.println("No rules applicable to the node [" + node + "] from the state [" + currentState + "] and the stack [" + stack + "]");
        return false;
    }

    private boolean applyRule(final Rule rule) {
        final String msg = "-".repeat(debugTab) + "| Node [" + node + "], stack " + stack + ", applying rule [" + rule + "]...";
        System.out.println(msg);
        if (rule.getxStack() != null && rule.getxStack().length == 1) {
            results.put(rule.getxStack()[0], node);
        }

        if (rule.nextStates().length == 1 && rule.nextStates()[0].endsWith("!")) {
            System.out.println("-".repeat(debugTab) + "!!!");
            System.out.println("-".repeat(debugTab) + "..." + results);
            return true;
        }

        final Stack<String> stackCopy = copyStack(stack);
        if (!Rule.EMPTY.equals(rule.stackRead())) {
            stackCopy.pop();
        }
        if (!Rule.EMPTY.equals(rule.stackWrite())) {
            stackCopy.push(rule.stackWrite());
        }
        if (Rule.EMPTY.equals(rule.input())) {
            // single node case essentially
            final TreeAutomaton nodeAutomaton = new TreeAutomaton(debugTab + 1, rulesTable, accordanceStack, stackCopy, rule.nextStates()[0], node);
            boolean result = nodeAutomaton.parse();
            if (result && results.isEmpty()) {
                results.putAll(transformIfNeeded(rule, nodeAutomaton.results));
            }
            System.out.println("-".repeat(debugTab) + "..." + result);
            System.out.println("-".repeat(debugTab) + "> " + node + ", " + rule + "::: " + results);
            return result;
        } else {
            final String[] nextStates = rule.nextStates();
            if (nextStates.length != node.nodes().size()) {
                System.err.println("The number of nodes of the node [" + node + "] differs from the number of states of the rule [" + rule + "]");
                return false;
            }
            final List<Map<String, Node>> resultsList = new ArrayList<>();
            for (int i = 0; i < nextStates.length; i++) {
                final String nextState = nextStates[i];
                final TreeAutomaton nodeAutomaton = new TreeAutomaton(debugTab + 1, rulesTable, accordanceStack, stackCopy, nextState, node.node(i));
                if (!nodeAutomaton.parse()) {
                    System.out.println("-".repeat(debugTab) + "..." + false);
                    return false;
                }
                resultsList.add(transformIfNeeded(rule, nodeAutomaton.getResults()));
            }
            System.out.println("-".repeat(debugTab) + "..." + rule + ": " + resultsList);
            boolean result = validateResultsAndMergeWithLocal(resultsList);
            System.out.println("-".repeat(debugTab) + "..." + result);
            // merge results
            resultsList.forEach(this.results::putAll);
            System.out.println("-".repeat(debugTab) + "> " + node + ", " + rule + "::: " + results);
            return result;
        }
    }

    private Map<String, Node> transformIfNeeded(final Rule rule, final Map<String, Node> results) {
        if (rule.getxStack() == null) {
            return results;
        }

        final Map<String, String> transformMap = new HashMap<>();
        for (int i = 1; i <= rule.getxStack().length; i++) {
            transformMap.put("x" + i, rule.getxStack()[i - 1]);
        }

        final Map<String, Node> transformedResults = new HashMap<>();
        for (String key : results.keySet()) {
            final String transformedKey = transformMap.get(key);
            if (transformedKey == null) {
                throw new IllegalStateException("Cannot transform key: " + key);
            }
            transformedResults.put(transformedKey, results.get(key));
        }

        return transformedResults;
    }

    private boolean validateResultsAndMergeWithLocal(List<Map<String, Node>> resultsList) {
        final Set<String> keys = resultsList
                .stream()
                .map(Map::keySet)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());

        for (String key : keys) {
            Node lastNode = null;
            for (Map<String, Node> resultMap : resultsList) {
                if (lastNode == null) {
                    lastNode = resultMap.get(key);
                } else if (resultMap.containsKey(key) && !lastNode.equals(resultMap.get(key))) {
                    System.out.println("-".repeat(debugTab) + "x: " + resultsList);
                    return false;
                }
            }
        }

        if (results.isEmpty()) {
            for (String key : keys) {
                for (Map<String, Node> resultMap : resultsList) {
                    final Node value = resultMap.get(key);
                    if (value != null) {
                        results.put(key, value);
                        break;
                    }
                }
            }
        }

        return true;
    }

    private Map<String, Node> getResults() {
        return results;
    }

    private Stack<String> copyStack(final Stack<String> stack) {
        //noinspection unchecked
        return (Stack<String>) stack.clone();
    }

    private boolean tryRule(final Rule rule) {
        final Stack<String> stackCopy = copyStack(stack);

        if (!Rule.EMPTY.equals(rule.input()) && !node.label().equals(rule.input())) {
            return false;
        }
        if (!Rule.EMPTY.equals(rule.stackRead()) && !stackCopy.pop().equals(rule.stackRead())) {
            return false;
        }

        return true;
    }
}
