package me.liuli.falcon.manager;

public class CheckResult {
    public static CheckResult PASSED = new CheckResult();
    public String message;
    private boolean isFail;

    public CheckResult(String message) {
        isFail = true;
        this.message = message;
    }

    private CheckResult() {
        isFail = false;
        this.message = "Passed";
    }

    public boolean failed() {
        return isFail;
    }
}
