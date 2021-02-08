package me.liuli.falcon.check.movement;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.level.GlobalBlockPalette;
import cn.nukkit.level.Level;
import cn.nukkit.level.Location;
import cn.nukkit.level.Position;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.Vector3;
import cn.nukkit.network.protocol.UpdateBlockPacket;
import me.liuli.falcon.manager.CheckResult;
import me.liuli.falcon.manager.CheckType;

import java.util.ArrayList;

public class NoClipCheck {
    public static CheckResult check(Player player,Location from) {
        AxisAlignedBB newBB = player.getBoundingBox();

        AxisAlignedBB bb2 = newBB.clone();
        bb2.expand(-0.2, -0.2, -0.2);

        ArrayList<Block> blocksAround = getBlocksAround(player.level, newBB);
        ArrayList<Block> collidingBlocks = getCollisionBlocks(blocksAround, newBB);

        for (Block block : collidingBlocks) {
            if (!block.canPassThrough()) {
                AxisAlignedBB bb = block.getBoundingBox();
                if (bb != null && bb.getMaxY() - newBB.getMinY() >= 0.6 && block.collidesWithBB(bb2)) {
                    //update block for lagging player
                    UpdateBlockPacket packet = new UpdateBlockPacket();
                    packet.x = block.getFloorX();
                    packet.y = block.getFloorY();
                    packet.z = block.getFloorZ();
                    packet.dataLayer = 0;
                    packet.blockRuntimeId = GlobalBlockPalette.getOrCreateRuntimeId(block.getId(), block.getDamage());
                    player.dataPacket(packet);

                    if(CheckType.NOCLIP.otherData.getBoolean("smartFlag")){
                        for(int i = (int) from.y+2; i<255; i++){
                            Position pos=Position.fromObject(new Vector3(from.x,i, from.z),from.level);
                            if(pos.getLevelBlock().getId()==Block.AIR){
                                player.teleport(pos);
                                return new CheckResult("Trying to move into " + block.getName());
                            }
                        }
                    }

                    return new CheckResult("Trying to move into " + block.getName());
                }
            }
        }
        return CheckResult.PASSED;
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
