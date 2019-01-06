package com.github.andremoniy;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

class GrammarToRulesConverter {

    private static class ParsedGrammarRule {
        private final Tree lhs;
        private final Tree rhs;

        private ParsedGrammarRule(final Tree lhs, final Tree rhs) {
            this.lhs = lhs;
            this.rhs = rhs;
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

        final Set<Rule> rules = new HashSet<>();

        for (ParsedGrammarRule parsedGrammarRule : parsedGrammarRules) {
            final String label = parsedGrammarRule.rhs.label();
            if (parsedGrammarRule.rhs.nodes().isEmpty() && (Character.isLowerCase(label.charAt(0)))) {
                rules.add(new Rule("0", "e", "e", "$", label));
            }
        }

        final Set<Rule> rulesToAddSet = new HashSet<>();
        Set<Rule> rulesToExpand = rules;
        do {
            for (Rule rule : rulesToExpand) {
                for (String state : rule.nextStates()) {
                    final String indexReplacedState = state.replace("[]", "[..]");
                    for (ParsedGrammarRule parsedGrammarRule : parsedGrammarRules) {
                        if (state.contains("[]") && parsedGrammarRule.rhs.toString().contains(indexReplacedState)) {
                            rulesToAddSet.add(new Rule(state, "e", "e", "e", parsedGrammarRule.rhs.label()));
                        }

                        if (parsedGrammarRule.rhs.toString().contains(state)) {
                            if (!parsedGrammarRule.rhs.label().equals(state) && Character.isLowerCase(parsedGrammarRule.rhs.label().indexOf(0))) {
                                final String input = parsedGrammarRule.rhs.label();
                                final String stackWrite = parsedGrammarRule.lhs.stack().isEmpty() ? "e" : parsedGrammarRule.lhs.stack().getFirst();
                                final String[] nextStates = parsedGrammarRule.rhs.nodes().stream().map(
                                        node -> node.label().startsWith("x") ? findPredecessor(parsedGrammarRule, node, parsedGrammarRules) : node.label()
                                ).toArray(String[]::new);
                                rulesToAddSet.add(new Rule(
                                        state,
                                        input,
                                        "e",
                                        stackWrite,
                                        nextStates
                                ));
                            }
                        }
                    }
                }
            }
            boolean anythingNewHasBeenAdded = rules.addAll(rulesToAddSet);
            if (!anythingNewHasBeenAdded) {
                break;
            }
            rulesToExpand = rulesToAddSet;
        } while (!rulesToExpand.isEmpty());

        return List.copyOf(rules);
    }

    private static String findPredecessor(
            final ParsedGrammarRule parsedGrammarRule,
            final Node node,
            final List<ParsedGrammarRule> parsedGrammarRules
    ) {
        // ToDo:
        // ToDo: in fact this won't properly work if we have more than 1 free variable
        // ToDo: consider the case:
        // ToDo: K[a..](x1,x2) -> s(K[..](x1,x2),x1,x2)  ???
        return parsedGrammarRule.lhs.label();
    }
}
