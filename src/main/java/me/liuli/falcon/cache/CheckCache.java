package me.liuli.falcon.cache;

import cn.nukkit.Player;
import me.liuli.falcon.check.combat.fakePlayer.FakePlayer;
import me.liuli.falcon.check.combat.fakePlayer.FakePlayerManager;
import me.liuli.falcon.manager.AnticheatManager;
import me.liuli.falcon.manager.CheckType;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CheckCache {
    private static Map<String,CheckCache> checkCacheMap=new HashMap<>();
    public FakePlayer fakePlayer;
    public Player player;
    public String nametag;
    public long lastHurt;
    public int CombatVL=0,MovementVL=0,WorldVL=0,MiscVL=0;
    public boolean warn=false;

    public CheckCache(Player player){
        checkCacheMap.put(player.getName(),this);
        this.player=player;
        lastHurt=new Date().getTime();
        nametag=player.getNameTag();
        if(AnticheatManager.canCheckPlayer(player,CheckType.KA_BOT)) {
            fakePlayer = FakePlayerManager.spawnFake(player);
        }
    }
    public static CheckCache get(Player player){
        return get(player.getName());
    }
    public static CheckCache get(String name){
        return checkCacheMap.get(name);
    }
    public static CheckCache remove(String name){
        return checkCacheMap.remove(name);
    }
}
