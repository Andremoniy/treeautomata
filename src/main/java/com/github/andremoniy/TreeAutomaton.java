package com.github.andremoniy;

import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

public class TreeAutomaton {

    private final List<Rule> rulesTable;
    private final Stack<String> stack;
    private final String currentState;
    private final Node node;

    private TreeAutomaton(final List<Rule> rulesTable, final Stack<String> stack, final String currentState, final Node node) {
        this.rulesTable = rulesTable;
        this.stack = stack; // Don't copy!
        this.currentState = currentState;
        this.node = node;
    }

    public TreeAutomaton(final List<Rule> rulesTable, final Node node) {
        this(rulesTable, new Stack<>(), "0", node);
    }

    public boolean parse() {

        final List<Rule> possibleRules = rulesTable.stream().filter(rule -> rule.state().equals(currentState)).collect(Collectors.toList());
        final List<Rule> applicableRules = possibleRules.stream().filter(this::tryRule).collect(Collectors.toList());

        for (Rule rule : applicableRules) {
            if (applyRule(rule)) {
                return true;
            }
        }

        System.err.println("No rules applicable to the node [" + node + "] from state [" + currentState + "] and stack [" + stack + "]");
        return false;
    }

    private boolean applyRule(final Rule rule) {

        if (rule.nextStates().length == 1 && rule.nextStates()[0].endsWith("!")) {
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
            final TreeAutomaton nodeAutomaton = new TreeAutomaton(rulesTable, stackCopy, rule.nextStates()[0], node);
            return nodeAutomaton.parse();
        } else {
            final String[] nextStates = rule.nextStates();
            if (nextStates.length != node.nodes().size()) {
                System.err.println("Number of nodes of [" + node + "] differ from number of states of rule [" + rule + "]");
                return false;
            }
            for (int i = 0; i < nextStates.length; i++) {
                final String nextState = nextStates[i];
                final TreeAutomaton nodeAutomaton = new TreeAutomaton(rulesTable, stackCopy, nextState, node.node(i));
                if (!nodeAutomaton.parse()) {
                    return false;
                }
            }
        }
        return true;
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
