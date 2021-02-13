package me.liuli.falcon.check.combat;

import cn.nukkit.Player;
import cn.nukkit.level.Location;
import cn.nukkit.math.Vector3f;
import com.alibaba.fastjson.JSONObject;
import me.liuli.falcon.cache.CheckCache;
import me.liuli.falcon.manager.CheckResult;
import me.liuli.falcon.manager.CheckType;
import me.liuli.falcon.utils.LocationUtil;

public class AimbotCheck {
    public static CheckResult check(Player player, Location from, Location to) {
        CheckCache cache = CheckCache.get(player);
        if (cache == null)
            return CheckResult.PASSED;

        int dYaw = (int) Math.abs(to.getYaw() - from.getYaw());
        int dPitch = (int) Math.abs(to.getPitch() - from.getPitch());
        int moveLength = (int) Math.sqrt(Math.pow(dYaw, 2.0D) + Math.pow(dPitch, 2.0D));

        {
            if ((System.currentTimeMillis() - cache.teleportTime) < 1000) return CheckResult.PASSED;
            int maxRot = CheckType.AIMBOT.otherData.getInteger("maxRotate");
            int smoothMin = CheckType.AIMBOT.otherData.getInteger("smoothMin");
            int smoothSame = CheckType.AIMBOT.otherData.getInteger("smoothSame");

            if (moveLength > maxRot && moveLength < (360 - maxRot)) {
                return new CheckResult("Rotate too fast(speed=" + moveLength + ",max=" + maxRot + ")");
            }

            if (moveLength > smoothMin && (cache.lastRot / smoothMin) == (moveLength / smoothMin)) {
                cache.sameRot++;
            } else {
                cache.sameRot = 0;
            }
            cache.lastRot = moveLength;
            if (cache.sameRot > smoothSame) {
                return new CheckResult("Rotate too smooth(speed=" + moveLength + ",times=" + cache.sameRot + ")");
            }
        }

        //aimAccuracy
        {
            boolean increased=false;
            JSONObject checkConfig = CheckType.AIMBOT.otherData.getJSONObject("accuracy");
            if(moveLength>checkConfig.getInteger("minRot")) {
                for (Player viewPlayer : player.getViewers().values()) {
                    if (player.distance(viewPlayer) < checkConfig.getInteger("maxDist")) {
                        double yawDifference = KillauraCheck.calculateYawDifference(player.getLocation(), viewPlayer.getLocation());
                        double angleDifference = Math.abs(180 - Math.abs(Math.abs(yawDifference - player.yaw) - 180));
                        if (angleDifference < checkConfig.getInteger("maxDiff")) {
                            if (cache.onAimId == viewPlayer.getId()) {
                                cache.onAimTime++;
                                player.sendMessage("AIM: "+cache.onAimTime);
                                increased = true;
                                if (cache.onAimTime > checkConfig.getInteger("minTimes")) {
                                    return new CheckResult("aiming too accuracy(times=" + cache.onAimTime + ",diff=" + angleDifference + ")");
                                }
                            } else {
                                cache.onAimTime = 0;
                                cache.onAimId = viewPlayer.getId();
                            }
                        }
                    }
                }
                if (!increased) {
                    cache.onAimTime=Math.max(0,cache.onAimTime-2);
                }
            }
        }
        return CheckResult.PASSED;
    }
}
