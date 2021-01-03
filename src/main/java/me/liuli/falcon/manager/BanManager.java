package me.liuli.falcon.manager;

import cn.nukkit.Player;
import com.alibaba.fastjson.JSONObject;
import me.liuli.falcon.Main;
import me.liuli.falcon.cache.Configuration;
import me.liuli.falcon.utils.OtherUtils;

import java.io.File;
import java.util.UUID;

public class BanManager {
    private static JSONObject banJSON;
    private static String dataPath;
    public static void loadBanData(){
        dataPath=Main.plugin.getDataFolder().getPath()+"/data/ban.json";
        if(!new File(dataPath).exists()){
            OtherUtils.writeFile(dataPath,"{}");
        }
        banJSON=JSONObject.parseObject(OtherUtils.readFile(dataPath));
    }
    public static void addBan(Player player,long expire){
        String banid=banIdGen(player.getUniqueId());
        banJSON.put(banid,expire);
        saveBanData();
        checkBan(player);
    }
    public static void checkBan(Player player){
        String banid=banIdGen(player.getUniqueId());
        Long expire=banJSON.getLong(banid);
        if(expire!=null){
            if(OtherUtils.getTime()>expire&&expire!=-1){
                banJSON.remove(banid);
                saveBanData();
            }else{
                String time;
                if(expire==-1){
                    time="FOREVER";
                }else{
                    time=OtherUtils.t2s(expire-OtherUtils.getTime());
                }
                player.kick(Configuration.LANG.BAN_REASON.proc(new String[]{time,banid}),false);
            }
        }
    }
    private static void saveBanData(){
        OtherUtils.writeFile(dataPath,banJSON.toJSONString());
    }
    private static String banIdGen(UUID uuid){
        return uuid.toString().replaceAll("-","").toUpperCase();
    }
}
