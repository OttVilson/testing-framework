package com.vilson.tests.helpers;

import com.google.gson.Gson;
import com.vilson.environment.Environment;
import com.vilson.environment.EnvironmentConfigurations;

import java.util.Optional;

public class TestInitHelpers {

    public static Environment getProduction() {
        EnvironmentConfigurations ec = new EnvironmentConfigurations(new Gson());
        Optional<Environment> env = ec.getEnvironment("production");
        env.orElseThrow(() -> new RuntimeException("production was not present as an environment"));
        return env.get();
    }
}
