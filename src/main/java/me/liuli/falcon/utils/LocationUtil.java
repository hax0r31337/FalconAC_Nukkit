package me.liuli.falcon.utils;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockLiquid;
import cn.nukkit.level.Location;
import cn.nukkit.level.Position;
import cn.nukkit.math.Vector3;
import cn.nukkit.math.Vector3f;

public class LocationUtil {
    public static Location setDirection(Vector3f vector, Location location) {
        double x = vector.getX();
        double z = vector.getZ();
        if (x == 0.0D && z == 0.0D) {
            location.pitch = (float) (vector.getY() > 0.0D ? -90 : 90);
        } else {
            double theta = Math.atan2(-x, z);
            location.yaw = (float) Math.toDegrees((theta + 6.283185307179586D) % 6.283185307179586D);
            double x2 = x * x;
            double z2 = z * z;
            double xz = Math.sqrt(x2 + z2);
            location.pitch = (float) Math.toDegrees(Math.atan(-vector.getY() / xz));
        }
        return location;
    }

    public static boolean isHoveringOverWater(Location player, int blocks) {
        for (int i = player.getFloorY(); i > player.getFloorY() - blocks; i--) {
            Block newloc = (Position.fromObject(new Vector3(player.getFloorX(), i, player.getFloorZ()), player.level)).getLevelBlock();
            if (newloc.getId() != 0) {
                return (newloc instanceof BlockLiquid);
            }
        }
        return false;
    }

    public static Vector3 getDirection(Location location) {
        Vector3 vector = new Vector3();
        double rotX = location.getYaw();
        double rotY = location.getPitch();
        vector.y = -Math.sin(Math.toRadians(rotY));
        double xz = Math.cos(Math.toRadians(rotY));
        vector.x = -xz * Math.sin(Math.toRadians(rotX));
        vector.z = xz * Math.cos(Math.toRadians(rotX));
        return vector;
    }

    public static int floor(double num) {
        int floor = (int) num;
        return (double) floor == num ? floor : floor - (int) (Double.doubleToRawLongBits(num) >>> 63);
    }

    public static double distanceSquared(final double x1, final double y1, final double z1, final double x2, final double y2, final double z2) {
        final double dx = Math.abs(x1 - x2);
        final double dy = Math.abs(y1 - y2);
        final double dz = Math.abs(z1 - z2);
        return dx * dx + dy * dy + dz * dz;
    }

    public static double distanceSquared(final double x1, final double z1, final double x2, final double z2) {
        final double dx = Math.abs(x1 - x2);
        final double dz = Math.abs(z1 - z2);
        return dx * dx + dz * dz;
    }
}
