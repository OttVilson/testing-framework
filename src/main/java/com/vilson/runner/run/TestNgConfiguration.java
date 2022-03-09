package com.vilson.runner.run;

import org.testng.xml.XmlSuite;

public class TestNgConfiguration {

    private XmlSuite.ParallelMode parallelMode;
    private boolean preserveOrder;
    private int verbose;
    private int threadCount;

    XmlSuite.ParallelMode getParallelMode() {
        return parallelMode;
    }

    public void setParallelMode(XmlSuite.ParallelMode parallelMode) {
        this.parallelMode = parallelMode;
    }

    boolean isPreserveOrder() {
        return preserveOrder;
    }

    public void setPreserveOrder(boolean preserveOrder) {
        this.preserveOrder = preserveOrder;
    }

    int getVerbose() {
        return verbose;
    }

    public void setVerbose(int verbose) {
        this.verbose = verbose;
    }

    int getThreadCount() {
        return threadCount;
    }

    public void setThreadCount(int threadCount) {
        this.threadCount = threadCount;
    }

    @Override
    public String toString() {
        String form = "Conf with parallel mode %s, thread count %s, preserve order %b, verbose %d";
        return String.format(form, parallelMode, threadCount, preserveOrder, verbose);
    }
}
