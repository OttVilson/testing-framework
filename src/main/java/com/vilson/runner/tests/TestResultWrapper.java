package com.vilson.runner.tests;

import com.google.gson.annotations.SerializedName;

public class TestResultWrapper {

    private Result result;
    private Test test;
    private Exception exception;
    private String[] log;

    public Test getTest() {
        return test;
    }

    public void setTest(Test test) {
        this.test = test;
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

    public String[] getLog() {
        return log;
    }

    public void setLog(String[] log) {
        this.log = log;
    }


    public static class Exception {
        private String cause;
        @SerializedName("stack-trace")
        private String[] stackTrace;

        public String getCause() {
            return cause;
        }

        public void setCause(String cause) {
            this.cause = cause;
        }

        public String[] getStackTrace() {
            return stackTrace;
        }

        public void setStackTrace(String[] stackTrace) {
            this.stackTrace = stackTrace;
        }
    }

    public enum Result {
        SUCCESS,
        FAILURE,
        SKIP;
    }
}
