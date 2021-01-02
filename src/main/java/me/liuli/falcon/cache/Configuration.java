package me.liuli.falcon.cache;

import com.alibaba.fastjson.JSONObject;
import me.liuli.falcon.Main;
import me.liuli.falcon.manager.CheckCategory;
import me.liuli.falcon.manager.CheckType;
import me.liuli.falcon.manager.PunishResult;
import me.liuli.falcon.utils.OtherUtils;

import java.io.File;

public class Configuration {
    public enum LANG {
        ALERT_PREFIX,WARNING,DEBUG,KICK,BAN,KICK_REASON,BAN_REASON;
        private String str="";
        public String proc(String[] args){
            String s=new String(this.str);
            int count=1;
            for(String arg : args){
                s=s.replaceAll("%"+count,arg);
                count++;
            }
            return s;
        }
        public void setStr(String str){
            this.str=str.replaceAll("&","§");
        }
        public String proc(){
            return this.str;
        }
    }
    private static JSONObject configJSON,langJSON;
    public static boolean checkOp,consoleDebug,playerDebug,flag,punishBoardcast;
    public static int ban;
    public static void loadConfig(){
        if(!new File(Main.plugin.getDataFolder().getPath()+"/lang.yml").exists()){
            OtherUtils.readJar("lang.yml",Main.jarDir,Main.plugin.getDataFolder().getPath()+"/lang.yml");
        }
        langJSON=JSONObject.parseObject(OtherUtils.y2j(new File(Main.plugin.getDataFolder().getPath()+"/lang.yml")));
        LANG.ALERT_PREFIX.setStr(langJSON.getString("ALERT_PREFIX"));
        LANG.WARNING.setStr(langJSON.getString("WARNING"));
        LANG.DEBUG.setStr(langJSON.getString("DEBUG"));
        LANG.KICK.setStr(langJSON.getString("KICK"));
        LANG.BAN.setStr(langJSON.getString("BAN"));
        LANG.KICK_REASON.setStr(langJSON.getString("KICK_REASON"));
        LANG.BAN_REASON.setStr(langJSON.getString("BAN_REASON"));

        if(!new File(Main.plugin.getDataFolder().getPath()+"/config.yml").exists()){
            OtherUtils.readJar("config.yml",Main.jarDir,Main.plugin.getDataFolder().getPath()+"/config.yml");
        }
        configJSON=JSONObject.parseObject(OtherUtils.y2j(new File(Main.plugin.getDataFolder().getPath()+"/config.yml")));
        if(configJSON.getInteger("config-version")!=Main.CONFIG_VERSION){
            throw new IllegalArgumentException("WRONG CONFIG VERSION!PLEASE DELETE/UPDATE YOUR CONFIG!");
        }
        JSONObject checksJSON=configJSON.getJSONObject("checks"),moduleJSON=configJSON.getJSONObject("modules");

        //load common configs
        checkOp=configJSON.getBoolean("checkOp");
        consoleDebug=configJSON.getJSONObject("debug").getBoolean("console");
        playerDebug=configJSON.getJSONObject("debug").getBoolean("player");
        flag=configJSON.getBoolean("flag");
        ban=configJSON.getInteger("ban");
        punishBoardcast=configJSON.getBoolean("punish-boardcast");

        //load checks
        CheckCategory.COMBAT.vl=checksJSON.getJSONObject("combat").getInteger("vl");
        CheckCategory.COMBAT.minusVl=checksJSON.getJSONObject("combat").getInteger("vl-minus");
        CheckCategory.COMBAT.warnVl=checksJSON.getJSONObject("combat").getInteger("warn");
        CheckCategory.COMBAT.result= PunishResult.valueOf(checksJSON.getJSONObject("combat").getString("result"));
        CheckCategory.MOVEMENT.vl=checksJSON.getJSONObject("movement").getInteger("vl");
        CheckCategory.MOVEMENT.minusVl=checksJSON.getJSONObject("movement").getInteger("vl-minus");
        CheckCategory.MOVEMENT.warnVl=checksJSON.getJSONObject("movement").getInteger("warn");
        CheckCategory.MOVEMENT.result=PunishResult.valueOf(checksJSON.getJSONObject("movement").getString("result"));
        CheckCategory.WORLD.vl=checksJSON.getJSONObject("world").getInteger("vl");
        CheckCategory.WORLD.minusVl=checksJSON.getJSONObject("world").getInteger("vl-minus");
        CheckCategory.WORLD.warnVl=checksJSON.getJSONObject("world").getInteger("warn");
        CheckCategory.WORLD.result=PunishResult.valueOf(checksJSON.getJSONObject("world").getString("result"));
        CheckCategory.MISC.vl=checksJSON.getJSONObject("misc").getInteger("vl");
        CheckCategory.MISC.minusVl=checksJSON.getJSONObject("misc").getInteger("vl-minus");
        CheckCategory.MISC.warnVl=checksJSON.getJSONObject("misc").getInteger("warn");
        CheckCategory.MISC.result=PunishResult.valueOf(checksJSON.getJSONObject("misc").getString("result"));

        //load modules
        CheckType.KA.enable=moduleJSON.getJSONObject("killaura").getBoolean("enable");
        CheckType.KA.addVl=moduleJSON.getJSONObject("killaura").getInteger("vl");
        CheckType.KA.otherData.put("range_c",moduleJSON.getJSONObject("killaura").getFloat("range_c"));
        CheckType.KA.otherData.put("range_v",moduleJSON.getJSONObject("killaura").getFloat("range_v"));
        CheckType.KA.otherData.put("angle",moduleJSON.getJSONObject("killaura").getInteger("angle"));
        CheckType.KA_BOT.enable=moduleJSON.getJSONObject("killaura_bot").getBoolean("enable");
        CheckType.KA_BOT.addVl=moduleJSON.getJSONObject("killaura_bot").getInteger("vl");
    }
}
