package me.liuli.falcon.manager;

public enum CheckResult {
    PASSED(false), FAILED(true);

    private boolean isFail;
    private CheckResult(boolean fail){
        isFail=fail;
    }
    public boolean failed(){
        return isFail;
    }
}
