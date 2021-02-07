package me.liuli.falcon.check.world;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.level.Location;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.BlockIterator;
import me.liuli.falcon.manager.CheckResult;
import me.liuli.falcon.manager.CheckType;

import java.util.ArrayList;
import java.util.Arrays;

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

    public static CheckResult rayTraceCheck(Player player, Block block) {
        //LOL not useable
        return CheckResult.PASSED;
        /*
        Location location = player.add(0.0, player.getEyeHeight(), 0.0);
        AxisAlignedBB bb=block.getBoundingBox();
        if(bb==null){
            bb=nullBB;
        }

        ArrayList<Vector3> positions=new ArrayList<>();
        if (location.x > bb.getMaxX() || location.x < bb.getMinX()) {
            if (location.x > block.x) {
                //SIDE EAST
                positions.add(new Vector3(bb.getMaxX(), bb.getMaxY(), bb.getMaxZ()));
                positions.add(new Vector3(bb.getMaxX(), bb.getMaxY(), bb.getMinZ()));
                positions.add(new Vector3(bb.getMaxX(), bb.getMinY(), bb.getMinZ()));
                positions.add(new Vector3(bb.getMaxX(), bb.getMinY(), bb.getMaxZ()));
            } else {
                //SIDE WEST
                positions.add(new Vector3(bb.getMinX(), bb.getMaxY(), bb.getMaxZ()));
                positions.add(new Vector3(bb.getMinX(), bb.getMaxY(), bb.getMinZ()));
                positions.add(new Vector3(bb.getMinX(), bb.getMinY(), bb.getMinZ()));
                positions.add(new Vector3(bb.getMinX(), bb.getMinY(), bb.getMaxZ()));
            }
        }

        if (location.z > bb.getMaxZ() || location.z < bb.getMinZ()) {
            if (location.z > block.z) {
                //SIDE SOUTH
                positions.add(new Vector3(bb.getMinX(), bb.getMaxY(), bb.getMaxZ()));
                positions.add(new Vector3(bb.getMaxX(), bb.getMinY(), bb.getMaxZ()));
                positions.add(new Vector3(bb.getMinX(), bb.getMinY(), bb.getMaxZ()));
                positions.add(new Vector3(bb.getMaxX(), bb.getMaxY(), bb.getMaxZ()));
            } else {
                //SIDE NORTH
                positions.add(new Vector3(bb.getMinX(), bb.getMaxY(), bb.getMinZ()));
                positions.add(new Vector3(bb.getMaxX(), bb.getMinY(), bb.getMinZ()));
                positions.add(new Vector3(bb.getMinX(), bb.getMinY(), bb.getMinZ()));
                positions.add(new Vector3(bb.getMaxX(), bb.getMaxY(), bb.getMinZ()));
            }
        }

        if (location.y > bb.getMaxY() || location.y < bb.getMinY()) {
            if (location.y > block.y) {
                //SIDE UP
                positions.add(new Vector3(bb.getMinX(), bb.getMaxY(), bb.getMinZ()));
                positions.add(new Vector3(bb.getMaxX(), bb.getMaxY(), bb.getMinZ()));
                positions.add(new Vector3(bb.getMinX(), bb.getMaxY(), bb.getMaxZ()));
                positions.add(new Vector3(bb.getMaxX(), bb.getMaxY(), bb.getMaxZ()));
            } else {
                //SIDE DOWN
                positions.add(new Vector3(bb.getMinX(), bb.getMinY(), bb.getMinZ()));
                positions.add(new Vector3(bb.getMaxX(), bb.getMinY(), bb.getMinZ()));
                positions.add(new Vector3(bb.getMinX(), bb.getMinY(), bb.getMaxZ()));
                positions.add(new Vector3(bb.getMaxX(), bb.getMinY(), bb.getMaxZ()));
            }
        }

        if (positions.isEmpty()) { //inside the block probably
            return CheckResult.PASSED;
        }

        for (Vector3 corner : positions) {
            double x = corner.x - location.x;
            double y = corner.y - location.y;
            double z = corner.z - location.z;

            double diff = Math.abs(x) + Math.abs(z);

            double yaw = Math.toDegrees(-Math.atan2(x / diff, z / diff));
            double pitch = (y == 0) ? 0 : Math.toDegrees(-Math.atan2(y, Math.sqrt(x * x + z * z)));
            Block found = getTargetBlock(
                    new Location(location.x, location.y, location.z, yaw, pitch, location.getLevel()),
                    block,
                    (int)Math.ceil(corner.distance(location) + 2),
                    BlockID.AIR
            );

            if (corner.distanceSquared(location) <= 0.25 || Objects.equals(found, block)) {
                return CheckResult.PASSED;
            } else if (found == null) {
                return new CheckResult("Trying interact though block");
            }
        }

        return new CheckResult("Trying interact though block");
         */
    }

    private static Block getTargetBlock(Location pos, Vector3 target, int maxDistance, int transparent) {
        try {
            Block[] blocks = rayTrace(pos, target, maxDistance, 1);

            Block block = blocks[0];

            if (Arrays.binarySearch(new int[]{transparent}, block.getId()) < 0) {
                return block;
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    private static Block[] rayTrace(Location pos, Vector3 target, int maxDistance, int maxLength) {
        ArrayList<Block> blocks = new ArrayList<>();

        Vector3 direction = pos.getDirectionVector();
        BlockIterator itr = new BlockIterator(pos.getLevel(), pos, direction, 0.0, Math.min(15, maxDistance));

        while (itr.hasNext()) {
            Block block = itr.next();
            blocks.add(block);

            if (maxLength != 0 && blocks.size() > maxLength) {
                blocks.remove(0);
            }

            if (!block.isTransparent() || block == target) {
                break;
            }
        }

        return blocks.toArray(new Block[0]);
    }
}
