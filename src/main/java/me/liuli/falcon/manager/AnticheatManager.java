package me.liuli.falcon.manager;

import cn.nukkit.Player;
import cn.nukkit.Server;
import me.liuli.falcon.Main;
import me.liuli.falcon.cache.CheckCache;
import me.liuli.falcon.cache.Configuration;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

public class AnticheatManager {
    public static void addVL(CheckCache cache, CheckType checkType){
        int nowVL=-1,maxVL=-1;
        switch (checkType.category){
            case COMBAT:{
                cache.CombatVL+=checkType.addVl;
                if(cache.CombatVL>CheckCategory.COMBAT.warnVl){
                    warnPlayer(cache);
                }
                if(cache.CombatVL>CheckCategory.COMBAT.vl){
                    punishPlayer(cache,checkType.category);
                }
                nowVL=cache.CombatVL;
                maxVL=CheckCategory.COMBAT.vl;
                break;
            }
            case MOVEMENT:{
                cache.MovementVL+=checkType.addVl;
                if(cache.MovementVL>CheckCategory.MOVEMENT.warnVl){
                    warnPlayer(cache);
                }
                if(cache.MovementVL>CheckCategory.MOVEMENT.vl){
                    punishPlayer(cache,checkType.category);
                }
                nowVL=cache.MovementVL;
                maxVL=CheckCategory.MOVEMENT.vl;
                break;
            }
            case WORLD:{
                cache.WorldVL+=checkType.addVl;
                if(cache.WorldVL>CheckCategory.WORLD.warnVl){
                    warnPlayer(cache);
                }
                if(cache.WorldVL>CheckCategory.WORLD.vl){
                    punishPlayer(cache,checkType.category);
                }
                nowVL=cache.WorldVL;
                maxVL=CheckCategory.WORLD.vl;
                break;
            }
            case MISC:{
                cache.MiscVL+=checkType.addVl;
                if(cache.MiscVL>CheckCategory.MISC.warnVl){
                    warnPlayer(cache);
                }
                if(cache.MiscVL>CheckCategory.MISC.vl){
                    punishPlayer(cache,checkType.category);
                }
                nowVL=cache.MiscVL;
                maxVL=CheckCategory.MISC.vl;
                break;
            }
        }
        if(Configuration.playerDebug){
            cache.player.sendMessage(Configuration.LANG.DEBUG.proc(new String[]{cache.player.getName(), checkType.name(), String.valueOf(nowVL), String.valueOf(maxVL)}));
        }
        if(Configuration.consoleDebug){
            Main.plugin.getLogger().info(cache.player.getName()+" §7failed §b"+checkType.name()+" §fvl:"+nowVL+"/"+maxVL);
        }
    }
    public static void warnPlayer(CheckCache cache){
        if(!cache.warn) {
            cache.warn = true;
            cache.player.sendMessage(Configuration.LANG.ALERT_PREFIX.proc() + Configuration.LANG.WARNING.proc());
        }
    }
    public static void boardcastMessage(String str){
        for(Map.Entry<UUID, Player> entry:Server.getInstance().getOnlinePlayers().entrySet()){
            entry.getValue().sendMessage(Configuration.LANG.ALERT_PREFIX.proc()+str);
        }
        Main.plugin.getLogger().info(str);
    }
    public static void punishPlayer(CheckCache cache,CheckCategory category){
        switch (category.result){
            case KICK:{
                if(Configuration.punishBoardcast){
                    boardcastMessage(Configuration.LANG.KICK.proc(new String[]{cache.player.getName()}));
                }
                cache.player.kick(Configuration.LANG.KICK_REASON.proc(),false);
                break;
            }
            case BAN:{
                if(Configuration.punishBoardcast){
                    boardcastMessage(Configuration.LANG.BAN.proc(new String[]{cache.player.getName()}));
                }
                Date date=null;
                String reason;
                if(Configuration.ban!=-1) {
                    date = new Date();
                    date.setTime(date.getTime() + ((long) Configuration.ban * 60 * 1000));
                    reason=Configuration.ban+"";
                }else{
                    reason="FOREVER";
                }
                cache.player.kick(Configuration.LANG.BAN_REASON.proc(new String[]{reason}),false);
                Main.plugin.getServer().getNameBans().addBan(cache.player.getName(), Configuration.LANG.BAN_REASON.proc(new String[]{reason}), date, "FalconAC");
            }
        }
    }
    public static boolean canCheckPlayer(Player player,CheckType checkType){
        return checkType.enable;
    }
}
