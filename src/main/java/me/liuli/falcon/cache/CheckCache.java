package me.liuli.falcon.cache;

import cn.nukkit.Player;
import me.liuli.falcon.check.combat.fakePlayer.FakePlayer;
import me.liuli.falcon.check.combat.fakePlayer.FakePlayerManager;
import me.liuli.falcon.manager.AnticheatManager;
import me.liuli.falcon.manager.CheckType;

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
    public float combatVL = 0, movementVL = 0, worldVL = 0, miscVL = 0;
    public boolean warn = false;
    public boolean flagDisable = false;
    public long teleportTime;
    public long lastPacketFlag;
    public long lastJump;
    //fast place check
    public long lastPlace;
    //aimbot check
    public int lastRot = 0, sameRot = 0;
    public long onAimId = -1, onAimTime = 0;
    //noswing check
    public long lastSwing;
    //timer check
    public long lastMovePacket;
    public double packetBalance = 0;
    //badpackets check
    public long lastAnimate;

    public CheckCache(Player player) {
        long timeNow = System.currentTimeMillis();

        checkCacheMap.put(player.getUniqueId(), this);
        movementCache = new MovementCache();
        this.player = player;
        nametag = player.getNameTag();
        if (AnticheatManager.canCheckPlayer(player, CheckType.KA_BOT)) {
            fakePlayer = FakePlayerManager.spawnFake(player);
        }
        lastHurt = timeNow;
        teleportTime = timeNow;
        lastMovePacket = timeNow;
        lastPacketFlag = timeNow;
        lastPlace = timeNow;
        lastSwing = timeNow;
        lastJump = timeNow;
        lastAnimate = timeNow;
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

    public boolean inTeleportAccount() {
        return (System.currentTimeMillis() - teleportTime) < Configuration.accountForTeleports;
    }
}
