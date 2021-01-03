package me.liuli.falcon.utils;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockLiquid;
import cn.nukkit.level.Location;
import cn.nukkit.level.Position;
import cn.nukkit.math.Vector3;
import cn.nukkit.math.Vector3f;

public class LocationUtils {
    public static Location setDirection(Vector3f vector,Location location) {
        double x = vector.getX();
        double z = vector.getZ();
        if (x == 0.0D && z == 0.0D) {
            location.pitch = (float)(vector.getY() > 0.0D ? -90 : 90);
        } else {
            double theta = Math.atan2(-x, z);
            location.yaw = (float)Math.toDegrees((theta + 6.283185307179586D) % 6.283185307179586D);
            double x2 = x*x;
            double z2 = z*z;
            double xz = Math.sqrt(x2 + z2);
            location.pitch = (float)Math.toDegrees(Math.atan(-vector.getY() / xz));
        }
        return location;
    }
    public static boolean isHoveringOverWater(Location player, int blocks) {
        for (int i = player.getFloorY(); i > player.getFloorY() - blocks; i--) {
            Block newloc = (Position.fromObject(new Vector3(player.getFloorX(),i,player.getFloorZ()),player.level)).getLevelBlock();
            if (newloc.getId() != 0) {
                return (newloc instanceof BlockLiquid);
            }
        }
        return false;
    }
}
