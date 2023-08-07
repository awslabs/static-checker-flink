package com.amazonaws.kda.flinkchecker;

public class CheckResult {
    public String checkMessage;
    public String checkName;
    public boolean success;

    public CheckResult checkMessage(String checkMessage) {
        this.checkMessage = checkMessage;
        return this;
    }

    public CheckResult checkName(String checkName) {
        this.checkName = checkName;
        return this;
    }
    public CheckResult success(boolean success) {
        this.success = success;
        return this;
    }
}
