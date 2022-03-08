package com.vilson.runner.cli.operations;

public class Operation {

    public final static Operation EXIT = new Operation("EXIT");

    private final String name;

    public Operation(String name) {
        this.name = name;
    }

    public void process() {
    }

    @Override
    public String toString() {
        return name;
    }
}

