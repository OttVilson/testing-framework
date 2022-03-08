package com.vilson.runner.tests;

import org.testng.ISuite;
import org.testng.ISuiteListener;
import org.testng.ITestNGMethod;

import java.util.ArrayList;
import java.util.List;

// https://stackoverflow.com/questions/52943528/how-to-list-get-a-list-of-all-testng-test-methods
class TestSetExtractorDryRunListener implements ISuiteListener {

    private String dryrun;
    // https://github.com/krmahadevan/testng/commit/fbc9879d08690a054320bdf5701b4801a6496864
    private final String dryrunKey = "testng.mode.dryrun";

    private final List<ITestNGMethod> tests = new ArrayList<>();

    @Override
    public void onStart(ISuite suite) {
        dryrun = System.getProperty(dryrunKey);
        System.setProperty(dryrunKey, "true");

        tests.addAll(suite.getAllMethods());
    }

    @Override
    public void onFinish(ISuite suite) {
        if (dryrun == null)
            System.clearProperty(dryrunKey);
        else
            System.setProperty(dryrunKey, dryrun);
    }

    List<ITestNGMethod> getTests() {
        return tests;
    }
}
