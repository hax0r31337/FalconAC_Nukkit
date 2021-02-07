package me.liuli.falcon.check.combat;

import cn.nukkit.Player;
import cn.nukkit.event.player.PlayerMoveEvent;
import me.liuli.falcon.cache.CheckCache;
import me.liuli.falcon.manager.CheckResult;
import me.liuli.falcon.manager.CheckType;

import java.util.UUID;

public class AimbotCheck {
    public static CheckResult check(Player player, PlayerMoveEvent event) {
        CheckCache cache = CheckCache.get(player);
        if (cache == null)
            return CheckResult.PASSED;

        if ((System.currentTimeMillis() - cache.lastTPTime) < 1000) return CheckResult.PASSED;
        int maxRot = CheckType.AIMBOT.otherData.getInteger("maxRotate");
        int smoothMin = CheckType.AIMBOT.otherData.getInteger("smoothMin");
        int smoothSame = CheckType.AIMBOT.otherData.getInteger("smoothSame");

        int dYaw = (int) Math.abs(event.getTo().getYaw() - event.getFrom().getYaw());
        int dPitch = (int) Math.abs(event.getTo().getPitch() - event.getFrom().getPitch());
        int moveLength = (int) Math.sqrt(Math.pow(dYaw, 2.0D) + Math.pow(dPitch, 2.0D));
        if (moveLength > maxRot && moveLength < (360 - maxRot)) {
            return new CheckResult("Rotate too fast(speed=" + moveLength + ",max=" + maxRot + ")");
        }

        UUID playerUUID = player.getUniqueId();
        if (moveLength > smoothMin && (cache.lastRot / smoothMin) == (moveLength / smoothMin)) {
            cache.sameRot++;
        } else {
            cache.sameRot = 0;
        }
        cache.lastRot = moveLength;
        if (cache.sameRot > smoothSame) {
            return new CheckResult("Rotate too smooth(speed=" + moveLength + ",times=" + cache.sameRot + ")");
        }
        return CheckResult.PASSED;
    }
}
