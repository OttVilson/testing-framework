package com.vilson.runner.tests;

import com.google.gson.Gson;
import com.vilson.environment.Environment;
import com.vilson.environment.EnvironmentConfigurations;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TestsRepository {

    private final List<Test> tests;

    public TestsRepository(Gson gson) {
        EnvironmentConfigurations ec = new EnvironmentConfigurations(gson);
        Optional<Environment> defaultEnvironment = ec.getEnvironment("default");

        defaultEnvironment.orElseThrow(() -> new NoSuchElementException("Default environment is missing"));

        TestsExtractor extractor = new TestsExtractor(defaultEnvironment.get());
        tests = extractor.getTests();
    }

    public List<Test> getAll() {
        return new ArrayList<>(tests);
    }

    public List<Test> getByPackage(String packageName) {
        return byPackageStream(packageName).collect(Collectors.toList());
    }

    public List<Test> getByClass(String className, String packageName) {
        return byClassStream(className, packageName).collect(Collectors.toList());
    }

    public List<Test> getByMethod(String methodName, String className, String packageName) {
        return byClassStream(className, packageName)
                .filter(hasName(methodName))
                .collect(Collectors.toList());
    }

    public List<Test> getByGroup(String group) {
        return tests.stream()
                .filter(hasGroup(group))
                .collect(Collectors.toList());
    }

    public Set<String> getAllGroups() {
        return tests.stream()
                .map(Test::getGroups)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
    }

    private Stream<Test> byPackageStream(String packageName) {
        return tests.stream().filter(isFromPackage(packageName));
    }

    private Stream<Test> byClassStream(String className, String packageName) {
        return byPackageStream(packageName).filter(isFromClass(className));
    }

    private Predicate<Test> isFromPackage(String packageName) {
        return test -> test.getPackageName().equals(packageName);
    }

    private Predicate<Test> isFromClass(String className) {
        return test -> test.getClassName().equals(className);
    }

    private Predicate<Test> hasName(String methodName) {
        return test -> test.getMethodName().equals(methodName);
    }

    private Predicate<Test> hasGroup(String group) {
        return test -> test.getGroups().contains(group);
    }
}
