package com.vilson.runner.cli.operations;

import com.google.gson.Gson;
import com.vilson.runner.cli.chooser.SingleChooser;
import com.vilson.runner.tests.TestResultWrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

class TestDetails {

    private final SingleChooser<TestResultWrapper> testChooser;
    private final SingleChooser<Operation> operationChooser;
    private List<Operation> operations;
    private final Gson gson;

    TestDetails(List<TestResultWrapper> tests, Scanner scanner, Gson gson) {
        testChooser = new SingleChooser<>(tests, scanner,
                wrapper -> wrapper.getResult().toString() + " " + wrapper.getTest().getMethodName());
        operations = new ArrayList<>();
        this.gson = gson.newBuilder().setPrettyPrinting().create();
        populateOperations();
        operationChooser = new SingleChooser<>(operations, scanner, String::valueOf);
    }

    void check() {
        while (true) {
            Operation op = operationChooser.chooseFromList();
            if (op.equals(Operation.EXIT)) break;
            op.process();
        }
    }

    private void populateOperations() {
        operations.add(Operation.EXIT);
        operations.add(new CheckTestDetails());
    }

    private class CheckTestDetails extends Operation {

        CheckTestDetails() {
            super("CHECK TEST DETAILS");
        }

        @Override
        public void process() {
            TestResultWrapper wrapper = testChooser.chooseFromList();
            System.out.println(gson.toJson(wrapper));
        }
    }
}
