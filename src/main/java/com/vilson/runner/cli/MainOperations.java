package com.vilson.runner.cli;

import com.google.gson.Gson;
import com.vilson.environment.Environment;
import com.vilson.environment.EnvironmentConfigurations;
import com.vilson.runner.cli.chooser.SingleChooser;
import com.vilson.runner.cli.operations.Operation;
import com.vilson.runner.cli.operations.ReportCheck;
import com.vilson.runner.cli.operations.TestsRunner;
import com.vilson.runner.run.TestRun;
import com.vilson.runner.tests.TestsRepository;

import java.util.*;

import static com.vilson.runner.cli.operations.Operation.EXIT;

public class MainOperations {

    private final TestsRepository testsRepository;
    private final EnvironmentConfigurations environmentConfigurations;
    private final Scanner scanner;
    private final Gson gson;

    private final List<Operation> operations = new ArrayList<>();
    private final TestsRunner testsRunner;
    private final Map<String, TestRun> testRuns = new HashMap<>();

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
        this.environmentConfigurations = environmentConfigurations;
        testsRepository = new TestsRepository(gson);
        testsRunner = new TestsRunner(environmentConfigurations, testsRepository, scanner, gson);
        fillOperationsList();
    }

    List<Operation> getOperations() {
        return operations;
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
                ReportCheck reportCheck = new ReportCheck(testRuns.get(id), testsRepository, scanner, gson);
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
            TestRun run = testsRunner.singleRun();
            testRuns.put("Test run nr. " + ++numberOfTestRuns, run);
        }
    }
}
