package me.liuli.falcon.cache;

import com.alibaba.fastjson.JSONObject;
import me.liuli.falcon.Main;
import me.liuli.falcon.manager.CheckCategory;
import me.liuli.falcon.manager.CheckType;
import me.liuli.falcon.manager.PunishResult;
import me.liuli.falcon.utils.OtherUtils;

import java.io.File;
import java.util.Map;

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
        loadCategory(CheckCategory.COMBAT,checksJSON.getJSONObject("combat"));
        loadCategory(CheckCategory.MOVEMENT,checksJSON.getJSONObject("movement"));
        loadCategory(CheckCategory.WORLD,checksJSON.getJSONObject("world"));
        loadCategory(CheckCategory.MISC,checksJSON.getJSONObject("misc"));

        //load modules
        loadType(CheckType.KILLAURA,moduleJSON.getJSONObject("killaura"));
        loadType(CheckType.KA_BOT,moduleJSON.getJSONObject("killaura_bot"));
        loadType(CheckType.KA_NOSWING,moduleJSON.getJSONObject("killaura_noswing"));
        loadType(CheckType.AIMBOT,moduleJSON.getJSONObject("aimbot"));
        loadType(CheckType.CRITICALS,moduleJSON.getJSONObject("criticals"));
        loadType(CheckType.ILLEGAL_INTERACT,moduleJSON.getJSONObject("illegalinteract"));
        loadType(CheckType.FAST_PLACE,moduleJSON.getJSONObject("fastplace"));
    }
    private static void loadCategory(CheckCategory category,JSONObject data){
        category.vl=data.getInteger("vl");
        category.minusVl=data.getInteger("vl-minus");
        category.flagVl=data.getInteger("flag");
        category.warnVl=data.getInteger("warn");
        category.result=PunishResult.valueOf(data.getString("result"));
    }
    private static void loadType(CheckType checkType,JSONObject data){
        checkType.enable= (boolean) data.remove("enable");
        checkType.addVl= (int) data.remove("vl");
        for(Map.Entry<String, Object> entry:data.entrySet()){
            checkType.otherData.put(entry.getKey(),entry.getValue());
        }
    }
}
