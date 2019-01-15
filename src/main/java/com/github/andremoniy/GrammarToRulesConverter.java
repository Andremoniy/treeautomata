package com.github.andremoniy;

import java.util.*;

class GrammarToRulesConverter {

    private static class ParsedGrammarRule {
        private final Tree lhs;
        private final Tree rhs;

        private ParsedGrammarRule(final Tree lhs, final Tree rhs) {
            this.lhs = lhs;
            this.rhs = rhs;
        }

        @Override
        public String toString() {
            return lhs + " -> " + rhs;
        }
    }

    private static class PreState {
        private final String state;
        private final String variableName;

        PreState(final String state) {
            this.state = state + ("S".equals(state) ? "!" : "");
            this.variableName = null;
        }

        PreState(final String state, final String variableName) {
            this.state = state;
            this.variableName = variableName;
        }
    }

    private static class PreRule extends Rule {

        private final PreState nextPreStates[];

        PreRule(String state, String input, String stackRead, String stackWrite, String nextState) {
            this(state, input, stackRead, stackWrite, new PreState(nextState));
        }

        PreRule(String state, String input, String stackRead, String stackWrite, PreState... nextStates) {
            super(state, input, stackRead, stackWrite, Arrays.stream(nextStates).map(nextState -> nextState.state).toArray(String[]::new));
            nextPreStates = nextStates;
        }
    }

    static List<Rule> convert(final List<String> grammarRules) {
        final List<ParsedGrammarRule> parsedGrammarRules = new ArrayList<>();

        for (String grammarRule : grammarRules) {
            final String[] split = grammarRule.split("->");
            final String lhs = split[0].trim();
            final String rhs = split[1].trim();
            parsedGrammarRules.add(new ParsedGrammarRule(TreeParser.parse(lhs), TreeParser.parse(rhs)));
        }

        final Set<PreRule> rules = new LinkedHashSet<>();

        for (ParsedGrammarRule parsedGrammarRule : parsedGrammarRules) {
            final String label = parsedGrammarRule.rhs.label();
            if (parsedGrammarRule.rhs.nodes().isEmpty() && (Character.isLowerCase(label.charAt(0)))) {
                rules.add(new PreRule("0", "e", "e", "$", label));
            }
        }

        Set<PreRule> rulesToExpand = new LinkedHashSet<>(rules);
        do {
            final Set<PreRule> rulesToAddSet = new LinkedHashSet<>();
            for (PreRule rule : rulesToExpand) {
                for (PreState preState : rule.nextPreStates) {
                    final String state = preState.state;
                    if (state.endsWith("!")) {
                        continue;
                    }
                    if (state.endsWith("_R")) {
                        final String input = state.substring(0, state.indexOf("_"));
                        rulesToAddSet.add(new PreRule(state, input, Rule.EMPTY, Rule.EMPTY, input + "!"));
                        continue;
                    }
                    final Tree stateTree = TreeParser.parse(state);
                    final String indexReplacedState = state.replace("[]", "[..]");
                    for (ParsedGrammarRule parsedGrammarRule : parsedGrammarRules) {
                        // K[](x1) -> x1
                        // should be transformed to:
                        // x1, e, e->e, [K[](x1)]
                        if (stateTree.nodes().isEmpty() && parsedGrammarRule.rhs.equals(stateTree)) {
                            rulesToAddSet.add(new PreRule(state, Rule.EMPTY, Rule.EMPTY, Rule.EMPTY, parsedGrammarRule.lhs.toString()));
                        }

                        // K[a..](x1) -> s(K[..](x1), x1))
                        // should be tansformed to:
                        // K[](x1), s, e->a, K[..](x1), [K[a..](x1)+x1]
                        if (!stateTree.nodes().isEmpty() && parsedGrammarRule.rhs.toString().contains(indexReplacedState)) {
                            // if the root node is a terminal
                            if (isTerm(parsedGrammarRule.rhs)) {
                                rulesToAddSet.add(new PreRule(
                                        state,
                                        parsedGrammarRule.rhs.label(),
                                        Rule.EMPTY,
                                        getStackElement(parsedGrammarRule.lhs),
                                        getNextStates(parsedGrammarRule, parsedGrammarRule.rhs)
                                ));
                            }
                        }

                        // P[..](x1) -> K[..](s(a,x1))
                        // should be tansformed to:
                        // K[..](x1),e,e->e, [K[..](s(a,x1))]
                        if (
                                !parsedGrammarRule.rhs.equals(stateTree)
                                        && parsedGrammarRule.rhs.label().equals(stateTree.label())
                                        && !(!stateTree.stack().isEmpty() && parsedGrammarRule.rhs.stack() == Node.EMPTY_STACK)
                                        && !(stateTree.stack() == Node.EMPTY_STACK && !parsedGrammarRule.rhs.stack().isEmpty())
                                        && !stateTree.equals(parsedGrammarRule.rhs)
                        ) {
                            if (hasVariable(preState)) {
                                rulesToAddSet.add(new PreRule(state, Rule.EMPTY, getStackElement(stateTree, parsedGrammarRule), getStackElement(parsedGrammarRule.lhs), new PreState(parsedGrammarRule.rhs.toString(), preState.variableName)));
                            } else {
                                rulesToAddSet.add(new PreRule(state, Rule.EMPTY, Rule.EMPTY, Rule.EMPTY, parsedGrammarRule.rhs.toString()));
                            }
                        }

                        // P[..](x1) -> K[..](s(a,x1))
                        // should be tansformed to:
                        // K[..](x1),s,e->e, [a_R, P[..](x1)]
                        if (hasVariable(preState) && parsedGrammarRule.rhs.equals(stateTree)) {
                            // take only that argument of RHS which corresponds to the current index!!
                            final int variableIndex = Integer.parseInt(preState.variableName.substring(1));
                            final Node correspondingNode = parsedGrammarRule.rhs.node(variableIndex - 1);
                            rulesToAddSet.add(new PreRule(
                                    state,
                                    isTerm(correspondingNode) ? correspondingNode.label() : Rule.EMPTY,
                                    Rule.EMPTY,
                                    getStackElement(parsedGrammarRule.lhs),
                                    getNextStates(parsedGrammarRule, correspondingNode)
                            ));
                        }
                    }
                }
            }
            boolean anythingNewHasBeenAdded = rules.addAll(rulesToAddSet);
            if (!anythingNewHasBeenAdded) {
                break;
            }
            rulesToExpand = new LinkedHashSet<>(rulesToAddSet);
        } while (!rulesToExpand.isEmpty());

        filterNoobRules(rules);

        return List.copyOf(rules);
    }

    private static String getStackElement(final Node node, final ParsedGrammarRule parsedGrammarRule) {
        if (node.stack() != Node.EMPTY_STACK && node.stack() != null && parsedGrammarRule.rhs.stack() == Node.EMPTY_STACK) {
            return "$";
        }
        return getStackElement(parsedGrammarRule.rhs);
    }

    private static boolean hasVariable(PreState preState) {
        return preState.variableName != null && !preState.variableName.isEmpty();
    }

    private static PreState[] getNextStates(final ParsedGrammarRule parsedGrammarRule, final Node rhsNode) {
        if (isTerm(rhsNode) && rhsNode.nodes().isEmpty() && parsedGrammarRule.lhs.nodes().isEmpty()) {
            return new PreState[]{new PreState(parsedGrammarRule.lhs.toString())};
        }
        return rhsNode.nodes().stream()
                .map(node -> {
                    if (node.label().startsWith("x")) {
                        return new PreState(parsedGrammarRule.lhs.toString(), node.label());
                    } else {
                        return new PreState(wrapTerminalReader(node));
                    }
                })
                .toArray(PreState[]::new);
    }

    private static String wrapTerminalReader(final Node node) {
        if (node.nodes().isEmpty() && isTerm(node)) {
            return node.label() + "_R";
        }
        return node.toString();
    }

    private static boolean isTerm(final Node node) {
        return Character.isLowerCase(node.label().charAt(0));
    }

    private static String getStackElement(final Tree tree) {
        return tree.stack() == null || tree.stack().isEmpty() ? "e" : tree.stack().getFirst();
    }

    private static void filterNoobRules(final Set<PreRule> rules) {
        rules.removeIf(rule -> rule.nextStates().length == 0);
        rules.removeIf(rule -> rule.state().equals(rule.nextStates()[0])
                && rule.nextStates().length == 1
                && Rule.EMPTY.equals(rule.input())
                && Rule.EMPTY.equals(rule.stackRead())
                && Rule.EMPTY.equals(rule.stackWrite()));
    }
}
