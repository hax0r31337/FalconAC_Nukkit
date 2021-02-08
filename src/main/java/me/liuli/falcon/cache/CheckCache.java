package me.liuli.falcon.cache;

import cn.nukkit.Player;
import me.liuli.falcon.check.combat.fakePlayer.FakePlayer;
import me.liuli.falcon.check.combat.fakePlayer.FakePlayerManager;
import me.liuli.falcon.manager.AnticheatManager;
import me.liuli.falcon.manager.CheckType;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CheckCache {
    private static final Map<UUID, CheckCache> checkCacheMap = new HashMap<>();
    public MovementCache movementCache;
    public FakePlayer fakePlayer;
    public Player player;
    public String nametag;
    public long lastHurt;
    public int combatVL = 0, movementVL = 0, worldVL = 0, miscVL = 0;
    public boolean warn = false;
    public long lastTPTime;
    //global
    public long velocityTime;
    //fast place check
    public long lastPlace = 0L;
    //aimbot check
    public int lastRot = 0, sameRot = 0;
    //noswing check
    public long lastSwing = 0;
    //timer check
    public long lastMovePacket;
    public double packetBalance = 0;

    public CheckCache(Player player) {
        long timeNow = System.currentTimeMillis();

        checkCacheMap.put(player.getUniqueId(), this);
        movementCache = new MovementCache();
        this.player = player;
        lastHurt = new Date().getTime();
        nametag = player.getNameTag();
        if (AnticheatManager.canCheckPlayer(player, CheckType.KA_BOT)) {
            fakePlayer = FakePlayerManager.spawnFake(player);
        }
        lastTPTime = timeNow;
        lastMovePacket = timeNow;
        velocityTime = timeNow;
    }

    public boolean inVelocity(){
        return (System.currentTimeMillis() - velocityTime) < Configuration.globalValues.getInteger("velocity");
    }

    public void logVelocity(){
        velocityTime = System.currentTimeMillis();
    }

    public static CheckCache get(Player player) {
        return get(player.getUniqueId());
    }

    public static CheckCache get(UUID uuid) {
        return checkCacheMap.get(uuid);
    }

    public static void remove(Player player) {
        remove(player.getUniqueId());
    }

    public static void remove(UUID uuid) {
        checkCacheMap.remove(uuid);
    }
}
