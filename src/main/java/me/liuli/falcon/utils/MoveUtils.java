package me.liuli.falcon.utils;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.level.Location;
import cn.nukkit.level.Position;
import cn.nukkit.math.Vector3;
import me.liuli.falcon.other.BlockRelative;

public class MoveUtils {
    public static final double JUMP_MOTION_Y = 0.41999998688697815;

    public static boolean couldBeOnSlime(Location location) {
        return isNearBlock(Position.fromObject(new Vector3(fixXAxis(location.getX()), location.getY() - 0.01D,
                location.getZ()), location.getLevel()).getLevelBlock(), BlockID.SLIME_BLOCK)
                || isBlock(Position.fromObject(new Vector3(fixXAxis(location.getX()), location.getY() - 0.51D,
                location.getZ()), location.getLevel()).getLevelBlock(), BlockID.SLIME_BLOCK);
    }

    public static boolean couldBeOnIce(Location location) {
        return isNearBlock(Position.fromObject(new Vector3(fixXAxis(location.getX()), location.getY() - 0.01D,
                location.getZ()), location.getLevel()).getLevelBlock(), BlockID.ICE)
                || isNearBlock(Position.fromObject(new Vector3(fixXAxis(location.getX()), location.getY() - 0.51D,
                location.getZ()), location.getLevel()).getLevelBlock(), BlockID.ICE);
    }

    public static boolean isNearBlock(Position position, int id) {
        Block block = position.getLevelBlock();
        return isBlock(block, id)
                || isBlock(BlockRelative.getRelative(BlockRelative.NORTH, block), id)
                || isBlock(BlockRelative.getRelative(BlockRelative.SOUTH, block), id)
                || isBlock(BlockRelative.getRelative(BlockRelative.EAST, block), id)
                || isBlock(BlockRelative.getRelative(BlockRelative.WEST, block), id)
                || isBlock(BlockRelative.getRelative(BlockRelative.NORTH_EAST, block), id)
                || isBlock(BlockRelative.getRelative(BlockRelative.NORTH_WEST, block), id)
                || isBlock(BlockRelative.getRelative(BlockRelative.SOUTH_EAST, block), id)
                || isBlock(BlockRelative.getRelative(BlockRelative.SOUTH_WEST, block), id);
    }

    public static boolean isSurroundedByBlock(Position position, int id) {
        Block block = position.getLevelBlock();
        return isBlock(block, id)
                && isBlock(BlockRelative.getRelative(BlockRelative.NORTH, block), id)
                && isBlock(BlockRelative.getRelative(BlockRelative.SOUTH, block), id)
                && isBlock(BlockRelative.getRelative(BlockRelative.EAST, block), id)
                && isBlock(BlockRelative.getRelative(BlockRelative.WEST, block), id)
                && isBlock(BlockRelative.getRelative(BlockRelative.NORTH_EAST, block), id)
                && isBlock(BlockRelative.getRelative(BlockRelative.NORTH_WEST, block), id)
                && isBlock(BlockRelative.getRelative(BlockRelative.SOUTH_EAST, block), id)
                && isBlock(BlockRelative.getRelative(BlockRelative.SOUTH_WEST, block), id);
    }

    public static boolean isBlock(Block block, int id) {
        return block.getId() == id;
    }

    public static double fixXAxis(double x) {
        /* For Z axis, just use Math.round(xaxis); */
        double touchedX = x;
        double rem = touchedX - Math.round(touchedX) + 0.01D;
        if (rem < 0.30D) {
            touchedX = floor(x) - 1;
        }
        return touchedX;
    }

    public static int floor(double num) {
        int floor = (int) num;
        return (double) floor == num ? floor : floor - (int) (Double.doubleToRawLongBits(num) >>> 63);
    }

    public static boolean inBlock(Player player, int id) {
        return player.getPosition().getLevelBlock().getId() == id;
    }

    public static boolean isLiquid(Block block) {
        return block.getId() == Block.STILL_WATER || block.getId() == Block.STILL_LAVA;
    }
}
