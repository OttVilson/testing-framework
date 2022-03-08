package com.vilson.environment;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

class MergeRules {
    static Environment merge(Environment parent, Environment child) {
        Environment merged = new Environment();
        if (child.getUrl() != null)
            merged.setUrl(child.getUrl());
        else
            merged.setUrl(parent.getUrl());

        Set<String> testPackages = new HashSet<>();
        if (parent.getTestPackages() != null)
            testPackages.addAll(parent.getTestPackages());
        merged.setTestPackages(new ArrayList<>(testPackages));

        if (child.isHeadless() == null)
            merged.setHeadless(parent.isHeadless());
        else
            merged.setHeadless(child.isHeadless());

        return merged;
    }
}
