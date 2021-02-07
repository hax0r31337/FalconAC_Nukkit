package me.liuli.falcon.check.world;

import cn.nukkit.Player;
import cn.nukkit.Server;
import me.liuli.falcon.cache.CheckCache;
import me.liuli.falcon.manager.CheckResult;
import me.liuli.falcon.manager.CheckType;

public class TimerCheck {
    public static CheckResult runCheck(Player player) {
        if (Server.getInstance().getTicksPerSecond() < CheckType.TIMER.otherData.getFloat("minimumTps"))
            return CheckResult.PASSED;

        CheckCache cache = CheckCache.get(player);
        if (cache == null) return CheckResult.PASSED;
        long packetTimeNow = System.currentTimeMillis();
        double packetBalance = cache.packetBalance;
        long rate = packetTimeNow - cache.lastMovePacket;
        packetBalance += 50;
        packetBalance -= rate;
        int triggerBalance = CheckType.TIMER.otherData.getInteger("triggerBalance");
        int minimumClamp = CheckType.TIMER.otherData.getInteger("minimumClamp");
        if (packetBalance >= triggerBalance) {
            int ticks = (int) Math.round(packetBalance / 50);
            packetBalance = -1 * (triggerBalance / 2);
            return new CheckResult("Overshot timer by " + ticks + " tick(s)");
        } else if (packetBalance < -1 * minimumClamp) {
            // Clamp minimum, 50ms=1tick of lag leniency
            packetBalance = -1 * minimumClamp;
        }
        cache.packetBalance = packetBalance;
        cache.lastMovePacket = packetTimeNow;
        return CheckResult.PASSED;
    }

    public static void compensate(Player player) {
        CheckCache cache = CheckCache.get(player);
        if (cache == null) return;
        cache.packetBalance -= CheckType.TIMER.otherData.getInteger("teleportCompensation");
    }
}
