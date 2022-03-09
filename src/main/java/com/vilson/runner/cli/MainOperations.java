package com.vilson.runner.cli;

import com.google.gson.Gson;
import com.vilson.environment.Environment;
import com.vilson.environment.EnvironmentConfigurations;
import com.vilson.runner.cli.chooser.SingleChooser;
import com.vilson.runner.cli.operations.Operation;
import com.vilson.runner.cli.operations.ReportCheck;
import com.vilson.runner.cli.operations.TestsRunner;
import com.vilson.runner.run.TestRun;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.vilson.runner.cli.operations.Operation.EXIT;

public class MainOperations {

    private final Scanner scanner;
    private final Gson gson;

    private final List<Operation> operations = new ArrayList<>();
    private final TestsRunner testsRunner;
    private final Map<String, TestRun> testRuns = new HashMap<>();

    private final ExecutorService executorService = Executors.newFixedThreadPool(3);

    private int numberOfTestRuns = 0;

    MainOperations(Scanner scanner, Gson gson) {
        this(scanner, new EnvironmentConfigurations(gson), gson);
    }

    MainOperations(Scanner scanner, Environment environment, Gson gson) {
        this(scanner, new EnvironmentConfigurations(environment, gson), gson);
    }

    private MainOperations(Scanner scanner, EnvironmentConfigurations environmentConfigurations, Gson gson) {
        this.gson = gson;
        this.scanner = scanner;
        testsRunner = new TestsRunner(environmentConfigurations, scanner, gson);
        fillOperationsList();
    }

    List<Operation> getOperations() {
        return operations;
    }

    void stop() {
        executorService.shutdown();
    }

    private void fillOperationsList() {
        operations.add(EXIT);
        operations.add(new RunTestsFlow());
        operations.add(new CheckPreviousTestRuns());
    }

    private class CheckPreviousTestRuns extends Operation {
        CheckPreviousTestRuns() {
            super("CHECK PREVIOUS TEST RUNS");
        }

        @Override
        public void process() {
            if (testRuns.keySet().isEmpty())
                System.out.println("There are no previous test runs");
            else {
                SingleChooser<String> previousTestRunsChooser =
                        new SingleChooser<>(List.copyOf(testRuns.keySet()), scanner, String::valueOf);
                String id = previousTestRunsChooser.chooseFromList();
                ReportCheck reportCheck = new ReportCheck(testRuns.get(id), scanner, gson);
                reportCheck.check();
            }
        }
    }

    private class RunTestsFlow extends Operation {
        RunTestsFlow() {
            super("RUN TESTS");
        }

        @Override
        public void process() {
            int index = ++numberOfTestRuns;
            System.out.println("Started test run nr. " + index);
            TestRun testRun = testsRunner.singleRun();
            Runnable runTests = () -> {
                testRun.run();
                testRuns.put("Test run nr. " + index, testRun);
                System.out.println("Test run nr. " + index + " finished.");
            };
            executorService.submit(runTests);
        }
    }
}
