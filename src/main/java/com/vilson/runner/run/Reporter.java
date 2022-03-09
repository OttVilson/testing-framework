package com.vilson.runner.run;

import org.testng.*;
import org.testng.xml.XmlSuite;

import java.util.*;
import java.util.stream.Stream;

// https://www.baeldung.com/testng-custom-reporting
// https://stackoverflow.com/questions/45582864/how-to-integrate-the-reporter-loglogin-failed-with-custom-testng-report-usi
public class Reporter implements IReporter {

    private long start;
    private long stop;
    private final Set<ITestResult> failed = new HashSet<>();
    private final Set<ITestResult> skipped = new HashSet<>();
    private final Set<ITestResult> passed = new HashSet<>();

    @Override
    public void generateReport(List<XmlSuite> xmlSuites, List<ISuite> suites, String outputDirectory) {
        suites.stream()
                .map(ISuite::getResults)
                .map(Map::values)
                .flatMap(Collection::stream)
                .map(ISuiteResult::getTestContext)
                .forEach(this::populateSets);

        setStartAndEnd();
    }

    public Stream<ITestResult> getStreamOfAll() {
        return Stream.of(passed, failed, skipped).flatMap(Collection::stream);
    }

    public long getStart() {
        return start;
    }

    public long getStop() {
        return stop;
    }

    public List<ITestResult> getPassed() {
        return new ArrayList<>(passed);
    }

    public List<ITestResult> getFailed() {
        return new ArrayList<>(failed);
    }

    public List<ITestResult> getSkipped() {
        return new ArrayList<>(skipped);
    }

    private void populateSets(ITestContext context) {
        failed.addAll(context.getFailedTests().getAllResults());
        skipped.addAll(context.getSkippedTests().getAllResults());
        passed.addAll(context.getPassedTests().getAllResults());
    }

    private void setStartAndEnd() {
        Optional<Long> min = getStreamOfAll().map(ITestResult::getStartMillis).min(Long::compare);
        Optional<Long> max = getStreamOfAll().map(ITestResult::getEndMillis).max(Long::compare);

        min.ifPresent(start -> this.start = start);
        max.ifPresent(stop -> this.stop = stop);
    }
}
