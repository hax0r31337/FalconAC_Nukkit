package me.liuli.falcon.manager;

import com.alibaba.fastjson.JSONObject;

public enum CheckType {
    KILLAURA(CheckCategory.COMBAT),
    KA_BOT(CheckCategory.COMBAT),
    AIMBOT(CheckCategory.COMBAT),
    CRITICALS(CheckCategory.COMBAT),
    ILLEGAL_INTERACT(CheckCategory.WORLD),
    FAST_PLACE(CheckCategory.WORLD),
    TIMER(CheckCategory.WORLD),
    NOSWING(CheckCategory.MISC),
    BADPACKETS(CheckCategory.MISC);

    public boolean enable=false;
    public int addVl=1;
    public CheckCategory category;
    public JSONObject otherData=new JSONObject();
    private CheckType(CheckCategory category){
        this.category=category;
    }
}