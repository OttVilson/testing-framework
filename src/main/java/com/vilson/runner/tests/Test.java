package com.vilson.runner.tests;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Test {
    @SerializedName("package-name")
    private String packageName;
    @SerializedName("class-name")
    private String className;
    @SerializedName("method-name")
    private String methodName;
    private String description;
    @SerializedName("groups")
    private List<String> groups;

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getClassName() {
        return className;
    }

    public String getFQClassName() {
        return packageName + "." + className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public List<String> getGroups() {
        return groups;
    }

    public void setGroups(List<String> groups) {
        this.groups = groups;
    }

    @Override
    public String toString() {
        String form = "The test %s from class %s of package %s. Relevant groups are: %s";
        return String.format(form, methodName, className, packageName, String.join(", ", groups));
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
