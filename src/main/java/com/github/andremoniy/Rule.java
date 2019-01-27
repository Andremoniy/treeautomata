package com.github.andremoniy;

import java.util.Arrays;
import java.util.Objects;

public class Rule {

    public static final String EMPTY = "e";

    private final String state;
    private final String input;
    private final String stackRead;
    private final String stackWrite;
    private final String[] xStack;
    private final String[] nextStates;

    public Rule(
            final String state,
            final String input,
            final String stackRead,
            final String stackWrite,
            final String[] xStack,
            final String... nextStates
    ) {
        this.state = state;
        this.input = input;
        this.stackRead = stackRead;
        this.stackWrite = stackWrite;
        this.xStack = xStack;
        this.nextStates = nextStates;
    }

    public String state() {
        return state;
    }

    public String input() {
        return input;
    }

    public String stackRead() {
        return stackRead;
    }

    public String stackWrite() {
        return stackWrite;
    }

    public String[] nextStates() {
        return nextStates;
    }

    public String[] getxStack() {
        return xStack;
    }

    @Override
    public String toString() {
        return state + ": " + input + ", " + stackRead + " -> " + stackWrite + ", " + (xStack != null ? Arrays.toString(xStack) + ", " : "") + Arrays.toString(nextStates);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Rule rule = (Rule) o;
        return Objects.equals(state, rule.state) &&
                Objects.equals(input, rule.input) &&
                Objects.equals(stackRead, rule.stackRead) &&
                Objects.equals(stackWrite, rule.stackWrite) &&
                Arrays.equals(xStack, rule.xStack) &&
                Arrays.equals(nextStates, rule.nextStates);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(state, input, stackRead, stackWrite);
        result = 31 * result + Arrays.hashCode(xStack);
        result = 31 * result + Arrays.hashCode(nextStates);
        return result;
    }
}
