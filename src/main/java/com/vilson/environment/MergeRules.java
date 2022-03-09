package com.vilson.environment;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

class MergeRules {
    static Environment merge(Environment parent, Environment child) {
        Environment merged = new Environment();
        setUrl(parent, child, merged);
        setTestPackages(parent, merged);
        setHeadless(parent, child, merged);

        return merged;
    }

    private static void setUrl(Environment parent, Environment child, Environment merged) {
        if (child.getUrl() != null)
            merged.setUrl(child.getUrl());
        else
            merged.setUrl(parent.getUrl());
    }

    private static void setTestPackages(Environment parent, Environment merged) {
        Set<String> testPackages = new HashSet<>();
        if (parent.getTestPackages() != null)
            testPackages.addAll(parent.getTestPackages());
        merged.setTestPackages(new ArrayList<>(testPackages));
    }

    private static void setHeadless(Environment parent, Environment child, Environment merged) {
        if (child.isHeadless() == null)
            merged.setHeadless(parent.isHeadless());
        else
            merged.setHeadless(child.isHeadless());
    }
}
