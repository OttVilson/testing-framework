package com.vilson.runner.cli.operations;

import com.google.gson.Gson;
import com.vilson.runner.cli.chooser.SingleChooser;
import com.vilson.runner.tests.TestResultWrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class TestDetails {

    private final SingleChooser<TestResultWrapper> testChooser;
    private final SingleChooser<Operation> operationChooser;
    private List<Operation> operations;

    public TestDetails(List<TestResultWrapper> tests, Scanner scanner, Gson gson) {
        testChooser = new SingleChooser<>(tests, scanner, gson::toJson);
        operations = new ArrayList<>();
        fillOperations();
        operationChooser = new SingleChooser<>(operations, scanner, String::valueOf);
    }

    public void check() {
        while (true) {
            Operation op = operationChooser.chooseFromList();
            if (op.equals(Operation.EXIT)) break;
            op.process();
        }
    }

    private void fillOperations() {
        operations.add(Operation.EXIT);
        operations.add(new CheckTestDetails());
    }

    private class CheckTestDetails extends Operation {

        public CheckTestDetails() {
            super("CHECK TEST DETAILS");
        }

        @Override
        public void process() {
            TestResultWrapper wrapper = testChooser.chooseFromList();
            System.out.println("Tere");
        }
    }
}
