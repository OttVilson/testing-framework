package com.vilson.environment;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Environment {
    private String url;
    @SerializedName("test-packages")
    private List<String> testPackages;
    private Boolean headless;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<String> getTestPackages() {
        return testPackages;
    }

    public void setTestPackages(List<String> testPackages) {
        this.testPackages = testPackages;
    }

    @Override
    public String toString() {
        return url + " and " + (testPackages != null ? String.join(", ", testPackages) : "");
    }

    public Boolean isHeadless() {
        return headless;
    }

    public void setHeadless(Boolean headless) {
        this.headless = headless;
    }
}
