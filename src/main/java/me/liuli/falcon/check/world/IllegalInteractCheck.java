package me.liuli.falcon.check.world;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.level.Location;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.math.Vector3;
import me.liuli.falcon.manager.CheckResult;
import me.liuli.falcon.manager.CheckType;
import me.liuli.falcon.utils.LocationUtil;

public class IllegalInteractCheck {
    private static AxisAlignedBB nullBB = new SimpleAxisAlignedBB(0, 0, 0, 0, 0, 0);

    public static CheckResult isValidTarget(Player player, Block block) {
        Vector3 blockVec3 = new Vector3(block.x, block.y, block.z);
        double distance = player.getLocation().distance(blockVec3);
        double maxDistance = player.gamemode == 1
                ? CheckType.ILLEGAL_INTERACT.otherData.getDouble("creativeRange")
                : CheckType.ILLEGAL_INTERACT.otherData.getDouble("survivalRange");
        maxDistance += player.getPing()
                * CheckType.ILLEGAL_INTERACT.otherData.getDouble("pingCompensation");
        if (distance > maxDistance) {
            return new CheckResult("Trying to interact with a block out of view(distance=" + distance + ",max=" + maxDistance + ")");
        }
        return CheckResult.PASSED;
    }

    public static CheckResult rayTraceCheck(double x, double y, double z, float yaw, float pitch,
                                            int blockX, int blockY, int blockZ) {
        Location useLoc = new Location();
        useLoc.pitch = pitch;
        useLoc.yaw = yaw;
        Vector3 direction = LocationUtil.getDirection(useLoc);
        return CheckResult.PASSED;
    }
}
