package com.github.andremoniy;

import java.util.Arrays;
import java.util.Objects;

public class Rule {

    public static final String EMPTY = "e";

    private final String state;
    private final String input;
    private final String stackRead;
    private final String stackWrite;
    private final String[] nextStates;

    public Rule(String state, String input, String stackRead, String stackWrite, String... nextStates) {
        this.state = state;
        this.input = input;
        this.stackRead = stackRead;
        this.stackWrite = stackWrite;
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

    @Override
    public String toString() {
        return state + ": " + input + ", " + stackRead + " -> " + stackWrite + ", " + Arrays.toString(nextStates);
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
                Arrays.equals(nextStates, rule.nextStates);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(state, input, stackRead, stackWrite);
        result = 31 * result + Arrays.hashCode(nextStates);
        return result;
    }
}
