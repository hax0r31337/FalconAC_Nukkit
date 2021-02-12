package me.liuli.falcon.check.combat;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.level.Location;
import cn.nukkit.math.Vector3f;
import me.liuli.falcon.manager.CheckResult;
import me.liuli.falcon.manager.CheckType;
import me.liuli.falcon.utils.LocationUtil;

public class KillauraCheck {
    public static CheckResult checkAngle(Player player, EntityDamageByEntityEvent event) {
        Entity entity = event.getEntity();

        if (entity.isAlive()) {
            double yawDifference = calculateYawDifference(player.getLocation(), entity.getLocation());
            double angleDifference = Math.abs(180 - Math.abs(Math.abs(yawDifference - player.yaw) - 180));
            if (Math.round(angleDifference) > CheckType.KILLAURA.otherData.getInteger("angle")) {
                return new CheckResult("tried to attack from an illegal angle (angle=" + Math.round(angleDifference) + ")");
            }
        }
        return CheckResult.PASSED;
    }

    public static CheckResult checkReach(Player player, Entity target) {
        float allowedReach = player.gamemode != 1 ? CheckType.KILLAURA.otherData.getFloat("reach-common") : CheckType.KILLAURA.otherData.getFloat("reach-creative");
        if (player.gamemode == 1)
            allowedReach += 1.5D;
        allowedReach += player.getPing() * CheckType.KILLAURA.otherData.getFloat("reach-ping");
        if (target instanceof Player) {
            allowedReach += ((Player) target).getPing() * CheckType.KILLAURA.otherData.getFloat("reach-ping");
        }
        // Velocity compensation
        double reachedDistance = target.getLocation().distance(player.getLocation());
        if (reachedDistance > allowedReach)
            return new CheckResult("reached too far (distance=" + reachedDistance + ", max=" + allowedReach + ")");
        return CheckResult.PASSED;
    }

    public static double calculateYawDifference(Location from, Location to) {
        Location clonedFrom = from.clone();
        Vector3f vector = new Vector3f((float) (to.x - clonedFrom.x), (float) (to.y - clonedFrom.y), (float) (to.z - clonedFrom.z));
        return LocationUtil.setDirection(vector, clonedFrom).getYaw();
    }
}
