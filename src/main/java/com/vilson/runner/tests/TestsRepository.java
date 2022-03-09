package com.vilson.runner.tests;

import com.google.gson.Gson;
import com.vilson.environment.Environment;
import com.vilson.environment.EnvironmentConfigurations;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

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
}
