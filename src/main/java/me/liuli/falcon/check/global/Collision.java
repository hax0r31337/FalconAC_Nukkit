package me.liuli.falcon.check.global;

import cn.nukkit.block.Block;
import cn.nukkit.level.Level;
import cn.nukkit.level.Location;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.Vector3;

import java.util.ArrayList;

public class Collision {
    public static Block getCollisionBlock(AxisAlignedBB newBB, Location location) {
        AxisAlignedBB bb2 = newBB.clone();
        bb2.expand(-0.2, -0.2, -0.2);

        ArrayList<Block> blocksAround = getBlocksAround(location.level, newBB);
        ArrayList<Block> collidingBlocks = getCollisionBlocks(blocksAround, newBB);

        for (Block block : collidingBlocks) {
            if (!block.canPassThrough()) {
                AxisAlignedBB bb = block.getBoundingBox();
                if (bb != null && bb.getMaxY() - newBB.getMinY() >= 0.6 && block.collidesWithBB(bb2)) {
                    return block;
                }
            }
        }

        return null;
    }

    private static ArrayList<Block> getCollisionBlocks(ArrayList<Block> blocks, AxisAlignedBB bb) {
        ArrayList<Block> collisionBlocks = new ArrayList<>();
        for (Block b : blocks) {
            if (b.collidesWithBB(bb, true)) {
                collisionBlocks.add(b);
            }
        }
        return collisionBlocks;
    }

    private static ArrayList<Block> getBlocksAround(Level level, AxisAlignedBB bb) {
        ArrayList<Block> blocksAround = new ArrayList<>();
        Vector3 vector3 = new Vector3();
        bb.forEach((x, y, z) -> {
            Block block = level.getBlock(vector3.setComponents(x, y, z));
            blocksAround.add(block);
        });
        return blocksAround;
    }
}
