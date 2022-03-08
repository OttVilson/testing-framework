package com.vilson.runner.run;

import com.vilson.environment.Environment;
import org.testng.ITestContext;
import org.testng.ITestListener;

class VariablesProviderForTests implements ITestListener {

    private final Environment environment;

    VariablesProviderForTests(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void onStart(ITestContext context) {
        context.setAttribute("environment", environment);
    }
}
