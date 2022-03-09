package com.vilson.runner.run;

import com.vilson.environment.Environment;
import com.vilson.runner.tests.Test;
import com.vilson.runner.tests.TestsGrouper;
import org.testng.TestNG;
import org.testng.xml.XmlClass;
import org.testng.xml.XmlSuite;
import org.testng.xml.XmlTest;

import java.util.List;

public class TestRun {

    private final TestNgConfiguration conf;
    private final Environment environment;
    private final Reporter reporter = new Reporter();
    private final TestNG testNG;
    private final List<Test> tests;

    public TestRun(List<Test> tests, TestNgConfiguration conf, Environment environment) {
        this.tests = tests;
        this.conf = conf;
        this.environment = environment;
        this.testNG = getTestNG();
    }

    public void run() {
        runTests(tests, testNG);
    }

    public Reporter getReporter() {
        return reporter;
    }

    public TestNgConfiguration getConfiguration() {
        return conf;
    }

    public Environment getEnvironment() {
        return environment;
    }

    private void runTests(List<Test> tests, TestNG testNG) {
        XmlTest test = wireTest(testNG);
        List<XmlClass> classes = TestsGrouper.groupTestsByClass(tests);
        test.setXmlClasses(classes);
        testNG.run();
    }

    private XmlTest wireTest(TestNG testNG) {
        XmlSuite suite = new XmlSuite();
        testNG.setXmlSuites(List.of(suite));
        return new XmlTest(suite);
    }

    private TestNG getTestNG() {
        TestNG testNG = new TestNG();
        configureTestNg(testNG);
        testNG.addListener(new VariablesProviderForTests(environment));
        testNG.addListener(reporter);
        return testNG;
    }

    private void configureTestNg(TestNG testNG) {
        if (conf.getParallelMode() != null) testNG.setParallel(conf.getParallelMode());
        testNG.setPreserveOrder(conf.isPreserveOrder());
        if (conf.getThreadCount() != 0) testNG.setThreadCount(conf.getThreadCount());
        testNG.setVerbose(conf.getVerbose());
    }
}
