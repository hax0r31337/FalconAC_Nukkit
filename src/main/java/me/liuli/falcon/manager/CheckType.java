package me.liuli.falcon.manager;

import com.alibaba.fastjson.JSONObject;

public enum CheckType {
    KA_BOT(CheckCategory.COMBAT),KA(CheckCategory.COMBAT);

    public boolean enable=false;
    public int addVl=1;
    public CheckCategory category;
    public JSONObject otherData=new JSONObject();
    private CheckType(CheckCategory category){
        this.category=category;
    }
}