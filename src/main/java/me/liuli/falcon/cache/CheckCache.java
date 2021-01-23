package me.liuli.falcon.cache;

import cn.nukkit.Player;
import cn.nukkit.level.Position;
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
    public long lastTPTime;
    //fast place check
    public long lastPlace=0L;
    //aimbot check
    public int lastRot=0,sameRot=0;
    //noswing check
    public long lastSwing=0;
    //timer check
    public long lastMovePacket;
    public double packetBalance=0;

    public CheckCache(Player player){
        long timeNow=System.currentTimeMillis();

        checkCacheMap.put(player.getName(),this);
        this.player=player;
        lastHurt=new Date().getTime();
        nametag=player.getNameTag();
        if(AnticheatManager.canCheckPlayer(player,CheckType.KA_BOT)) {
            fakePlayer = FakePlayerManager.spawnFake(player);
        }
        lastTPTime=timeNow;
        lastMovePacket=timeNow;
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
