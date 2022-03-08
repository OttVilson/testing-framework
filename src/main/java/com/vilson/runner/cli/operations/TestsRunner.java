package com.vilson.runner.cli.operations;

import com.google.gson.Gson;
import com.vilson.environment.Environment;
import com.vilson.environment.EnvironmentConfigurations;
import com.vilson.runner.cli.chooser.MultiChooser;
import com.vilson.runner.cli.chooser.SingleChooser;
import com.vilson.runner.run.TestNgConfiguration;
import com.vilson.runner.run.TestRun;
import com.vilson.runner.tests.Test;
import com.vilson.runner.tests.TestsRepository;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

public class TestsRunner {

    private final EnvironmentConfigurations environmentConfigurations;

    private final SingleChooser<Environment> environmentChooser;
    private final MultiChooser<Test> testsChooser;
    private final ConfigurationInput configurationInput;

    public TestsRunner(EnvironmentConfigurations environmentConfigurations, TestsRepository testsRepository,
                       Scanner scanner, Gson gson) {
        this.environmentConfigurations = environmentConfigurations;

        environmentChooser = new SingleChooser<>(allEnvironments(), scanner, gson::toJson);
        testsChooser = new MultiChooser<>(testsRepository.getAll(), scanner, gson::toJson);
        configurationInput = new ConfigurationInput(scanner);
    }

    public TestRun singleRun() {
        Environment environment = environmentChooser.chooseFromList();
        TestNgConfiguration conf = configurationInput.getConfiguration();
        List<Test> chosenTests = testsChooser.chooseFromList();

        return new TestRun(chosenTests, conf, environment);
    }

    private List<Environment> allEnvironments() {
        List<String> allNames = environmentConfigurations.getListOfEnvironments();
        return allNames.stream()
                .map(environmentConfigurations::getEnvironment)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    /*
    public void tere() {
        EnvironmentConfigurations ec = new EnvironmentConfigurations();
        Environment production = ec.getEnvironment("production").get();

        List<Test> chosenOnes = testsChooser.chooseFromList();
        TestRun testRun = new TestRun(chosenOnes, new TestNgConfiguration(), production);
        // https://stackoverflow.com/questions/45582864/how-to-integrate-the-reporter-loglogin-failed-with-custom-testng-report-usi
        testRun.getReporter().getPassed().stream()
                .map(Reporter::getOutput)
                .flatMap(Collection::stream)
                .forEach(System.out::println);
        // testRun.getReporter().getFailed().stream().map(ITestResult::getThrowable)
        //        .map(Throwable::getMessage).forEach(System.out::println);
        /*
        testRun.getReporter().getFailed().stream().map(ITestResult::getThrowable)
                .map(Throwable::getStackTrace).map(
                s -> {
                    List<String> st = Arrays.stream(s).map(String::valueOf).collect(Collectors.toList());
                    return String.join("\r\n\tat ", st);
                }
        ).forEach(System.out::println);
    }
    */
}
