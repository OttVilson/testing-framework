package com.vilson.runner.tests;

import com.vilson.environment.Environment;
import com.vilson.runner.run.Reporter;
import org.testng.ITestNGMethod;
import org.testng.TestNG;
import org.testng.xml.XmlPackage;
import org.testng.xml.XmlSuite;
import org.testng.xml.XmlTest;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

class TestsExtractor {

    private final List<Test> tests = new ArrayList<>();

    TestsExtractor(Environment defaultEnvironment) {
        extractAllTestsFromDryRun(defaultEnvironment).stream()
                .map(this::composeTest)
                .forEach(tests::add);
    }

    List<Test> getTests() {
        return tests;
    }

    private List<ITestNGMethod> extractAllTestsFromDryRun(Environment e) {
        System.out.println("Dry run to extract all tests");
        TestNG dryRun = new TestNG();
        TestSetExtractorDryRunListener extractor = new TestSetExtractorDryRunListener();
        dryRun.addListener(extractor);
        dryRun.addListener(new Reporter());

        XmlSuite suite = getDryRunSuite();
        setUpTest(suite, e);

        dryRun.setXmlSuites(List.of(suite));
        dryRun.run();

        System.out.println("Finished dry run to extract all tests");
        return extractor.getTests();
    }

    private XmlSuite getDryRunSuite() {
        XmlSuite suite = new XmlSuite();
        suite.setParallel(XmlSuite.ParallelMode.METHODS);
        suite.setThreadCount(10);
        return suite;
    }

    private void setUpTest(XmlSuite suite, Environment e) {
        XmlTest test = new XmlTest(suite);
        List<XmlPackage> packages = e.getTestPackages().stream()
                .map(XmlPackage::new)
                .collect(Collectors.toList());
        test.setXmlPackages(packages);
    }

    private Test composeTest(ITestNGMethod method) {
        Class clazz = method.getTestClass().getRealClass();
        Test test = new Test();
        test.setMethodName(method.getMethodName());
        test.setClassName(clazz.getSimpleName());
        test.setPackageName(clazz.getPackageName());
        test.setDescription(method.getDescription());
        test.setGroups(List.of(method.getGroups()));
        return test;
    }
}
