package me.liuli.falcon.cache;

import com.alibaba.fastjson.JSONObject;
import me.liuli.falcon.FalconAC;
import me.liuli.falcon.manager.CheckCategory;
import me.liuli.falcon.manager.CheckType;
import me.liuli.falcon.manager.PunishResult;
import me.liuli.falcon.utils.OtherUtil;

import java.io.File;
import java.util.Map;

public class Configuration {
    public static boolean checkOp, consoleDebug, playerDebug, flag, punishBoardcast;
    public static int ban;
    private static JSONObject configJSON, langJSON;

    //global values
    public static int accountForTeleports;
    public static boolean smartFlag;

    public static void loadConfig() {
        if (!new File(FalconAC.plugin.getDataFolder().getPath() + "/lang.yml").exists()) {
            OtherUtil.writeFile(FalconAC.plugin.getDataFolder().getPath() + "/lang.yml", OtherUtil.getTextFromResource("lang.yml"));
        }
        langJSON = JSONObject.parseObject(OtherUtil.y2j(new File(FalconAC.plugin.getDataFolder().getPath() + "/lang.yml")));
        LANG.ALERT_PREFIX.setStr(langJSON.getString("ALERT_PREFIX"));
        LANG.WARNING.setStr(langJSON.getString("WARNING"));
        LANG.DEBUG.setStr(langJSON.getString("DEBUG"));
        LANG.KICK.setStr(langJSON.getString("KICK"));
        LANG.BAN.setStr(langJSON.getString("BAN"));
        LANG.KICK_REASON.setStr(langJSON.getString("KICK_REASON"));
        LANG.BAN_REASON.setStr(langJSON.getString("BAN_REASON"));

        if (!new File(FalconAC.plugin.getDataFolder().getPath() + "/config.yml").exists()) {
            OtherUtil.writeFile(FalconAC.plugin.getDataFolder().getPath() + "/config.yml", OtherUtil.getTextFromResource("config.yml"));
        }
        configJSON = JSONObject.parseObject(OtherUtil.y2j(new File(FalconAC.plugin.getDataFolder().getPath() + "/config.yml")));
        if (configJSON.getInteger("config-version") != FalconAC.CONFIG_VERSION) {
            throw new IllegalArgumentException("WRONG CONFIG VERSION!PLEASE DELETE/UPDATE YOUR CONFIG!");
        }
        JSONObject checksJSON = configJSON.getJSONObject("checks"), moduleJSON = configJSON.getJSONObject("modules");

        //load common configs
        checkOp = configJSON.getBoolean("checkOp");
        consoleDebug = configJSON.getJSONObject("debug").getBoolean("console");
        playerDebug = configJSON.getJSONObject("debug").getBoolean("player");
        flag = configJSON.getBoolean("flag");
        ban = configJSON.getInteger("ban");
        punishBoardcast = configJSON.getBoolean("punish-boardcast");

        //load checks
        loadCategory(CheckCategory.COMBAT, checksJSON.getJSONObject("combat"));
        loadCategory(CheckCategory.MOVEMENT, checksJSON.getJSONObject("movement"));
        loadCategory(CheckCategory.WORLD, checksJSON.getJSONObject("world"));
        loadCategory(CheckCategory.MISC, checksJSON.getJSONObject("misc"));

        //load modules
        //combat
        loadType(CheckType.KILLAURA, moduleJSON.getJSONObject("killaura"));
        loadType(CheckType.KA_BOT, moduleJSON.getJSONObject("killaura_bot"));
        loadType(CheckType.AIMBOT, moduleJSON.getJSONObject("aimbot"));
        loadType(CheckType.CRITICALS, moduleJSON.getJSONObject("criticals"));
        loadType(CheckType.VELOCITY, moduleJSON.getJSONObject("velocity"));
        //movement
        loadType(CheckType.SPEED, moduleJSON.getJSONObject("speed"));
        loadType(CheckType.FLIGHT, moduleJSON.getJSONObject("flight"));
        loadType(CheckType.STRAFE, moduleJSON.getJSONObject("strafe"));
        loadType(CheckType.WATER_WALK, moduleJSON.getJSONObject("waterwalk"));
        loadType(CheckType.NOCLIP, moduleJSON.getJSONObject("noclip"));
        //world
        loadType(CheckType.ILLEGAL_INTERACT, moduleJSON.getJSONObject("illegalinteract"));
        loadType(CheckType.FAST_PLACE, moduleJSON.getJSONObject("fastplace"));
        loadType(CheckType.TIMER, moduleJSON.getJSONObject("timer"));
        //misc
        loadType(CheckType.NOSWING, moduleJSON.getJSONObject("noswing"));
        loadType(CheckType.BADPACKETS, moduleJSON.getJSONObject("badpackets"));

        JSONObject globalValues=configJSON.getJSONObject("global");
        accountForTeleports=globalValues.getInteger("accountForTeleports");
        smartFlag=globalValues.getBoolean("smartFlag");

        //register smartFlag
        CheckType.VELOCITY.canSmartFlag=true;
        CheckType.NOCLIP.canSmartFlag=true;
    }

    private static void loadCategory(CheckCategory category, JSONObject data) {
        category.vl = data.getFloat("vl");
        category.minusVl = data.getFloat("vl-minus");
        category.passMinus = data.getFloat("pass-minus");
        category.flagVl = data.getFloat("flag");
        category.warnVl = data.getFloat("warn");
        category.result = PunishResult.valueOf(data.getString("result"));
    }

    private static void loadType(CheckType checkType, JSONObject data) {
        checkType.enable = data.getBoolean("enable");
        data.remove("enable");
        checkType.addVl = data.getFloat("vl");
        data.remove("vl");
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            checkType.otherData.put(entry.getKey(), entry.getValue());
        }
    }

    public enum LANG {
        ALERT_PREFIX, WARNING, DEBUG, KICK, BAN, KICK_REASON, BAN_REASON;
        private String str = "";

        public String proc(String[] args) {
            String s = new String(this.str);
            int count = 1;
            for (String arg : args) {
                s = s.replaceAll("%" + count, arg);
                count++;
            }
            return s;
        }

        public void setStr(String str) {
            this.str = str.replaceAll("&", "§");
        }

        public String proc() {
            return this.str;
        }
    }
}
