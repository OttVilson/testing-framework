package com.vilson.runner.cli.operations;

import com.google.gson.Gson;
import com.vilson.runner.cli.chooser.SingleChooser;
import com.vilson.runner.run.TestRun;
import com.vilson.runner.tests.Test;
import com.vilson.runner.tests.TestResultWrapper;
import com.vilson.runner.tests.TestsRepository;
import org.testng.ITestNGMethod;
import org.testng.ITestResult;
import org.testng.Reporter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class ReportCheck {

    private final TestRun testRun;
    private final TestsRepository testsRepository;
    private final List<Operation> operations = new ArrayList<>();
    private final SingleChooser<Operation> operationChooser;
    private final Gson gson;
    private final Scanner scanner;

    public ReportCheck(TestRun testRun, TestsRepository testsRepository, Scanner scanner, Gson gson) {
        this.testRun = testRun;
        this.testsRepository = testsRepository;
        fillOperationsList();
        operationChooser = new SingleChooser<>(operations, scanner, String::valueOf);
        this.gson = gson;
        this.scanner = scanner;
    }

    public void check() {
        while (true) {
            Operation op = operationChooser.chooseFromList();
            if (op.equals(Operation.EXIT)) break;
            op.process();
        }
    }

    private void fillOperationsList() {
        operations.add(Operation.EXIT);
        operations.add(new ViewConfiguration());
        operations.add(new ViewEnvironment());
        operations.add(new PrintTestRunResults());
    }

    private class ViewConfiguration extends Operation {

        private ViewConfiguration() {
            super("VIEW CONFIGURATION");
        }

        @Override
        public void process() {
            System.out.println(gson.toJson(testRun.getConfiguration()));
        }
    }

    private class ViewEnvironment extends Operation {

        private ViewEnvironment() {
            super("VIEW ENVIRONMENT");
        }

        @Override
        public void process() {
            System.out.println(gson.toJson(testRun.getEnvironment()));
        }
    }

    private class PrintTestRunResults extends Operation {

        private PrintTestRunResults() {
            super("PRINT TEST RUN RESULTS");
        }

        @Override
        public void process() {
            List<TestResultWrapper> tests = testRun.getReporter().getStreamOfAll()
                    .map(this::wrap)
                    .collect(Collectors.toList());
            tests.stream()
                    .map(wrap -> wrap.getResult() + " " + wrap.getTest().getMethodName())
                    .forEach(System.out::println);

            TestDetails testDetails = new TestDetails(tests, scanner, gson);
            testDetails.check();
        }

        private TestResultWrapper wrap(ITestResult iTestResult) {
            TestResultWrapper wrapper = new TestResultWrapper();
            wrapper.setResult(getStatus(iTestResult));
            Test test = new Test();
            ITestNGMethod method = iTestResult.getMethod();
            test.setPackageName(method.getRealClass().getPackageName());
            test.setClassName(method.getRealClass().getSimpleName());
            test.setMethodName(method.getMethodName());
            test.setDescription(method.getDescription());
            test.setGroups(List.of(method.getGroups()));
            wrapper.setTest(test);

            Throwable throwable = iTestResult.getThrowable();
            if (throwable != null) {
                TestResultWrapper.Exception exception = new TestResultWrapper.Exception();
                exception.setCause(throwable.getMessage());
                String[] stackTrace = Arrays.stream(throwable.getStackTrace())
                        .map(StackTraceElement::toString)
                        .toArray(String[]::new);
                exception.setStackTrace(stackTrace);
                wrapper.setException(exception);
            }

            List<String> log = Reporter.getOutput(iTestResult);
            wrapper.setLog(log.toArray(String[]::new));

            return wrapper;
        }

        private TestResultWrapper.Result getStatus(ITestResult iTestResult) {
            switch (iTestResult.getStatus()) {
                case ITestResult.SUCCESS:
                    return TestResultWrapper.Result.SUCCESS;
                case ITestResult.FAILURE:
                    return TestResultWrapper.Result.FAILURE;
                case ITestResult.SKIP:
                    return TestResultWrapper.Result.SKIP;
                default:
                    throw new IllegalArgumentException("No such status declared");
            }
        }
    }
}
