package me.liuli.falcon.manager;

public class CheckResult {
    public static CheckResult PASSED=new CheckResult();

    private boolean isFail;
    public String message;
    public CheckResult(String message){
        isFail=true;
        this.message=message;
    }
    private CheckResult(){
        isFail=false;
        this.message="Passed";
    }
    public boolean failed(){
        return isFail;
    }
}
