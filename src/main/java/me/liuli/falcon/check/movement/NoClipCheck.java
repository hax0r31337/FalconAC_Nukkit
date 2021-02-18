package me.liuli.falcon.check.movement;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.level.GlobalBlockPalette;
import cn.nukkit.level.Location;
import cn.nukkit.level.Position;
import cn.nukkit.math.Vector3;
import cn.nukkit.network.protocol.UpdateBlockPacket;
import me.liuli.falcon.cache.Configuration;
import me.liuli.falcon.check.global.Collision;
import me.liuli.falcon.manager.CheckResult;

public class NoClipCheck {
    public static CheckResult check(Player player, Location from) {
        Block block = Collision.getCollisionBlock(player.getBoundingBox(), from);

        if (block != null) {
            //update block for lagging player
            UpdateBlockPacket packet = new UpdateBlockPacket();
            packet.x = block.getFloorX();
            packet.y = block.getFloorY();
            packet.z = block.getFloorZ();
            packet.dataLayer = 0;
            packet.blockRuntimeId = GlobalBlockPalette.getOrCreateRuntimeId(block.getId(), block.getDamage());
            player.dataPacket(packet);

            if (Configuration.smartFlag) {
                for (int i = (int) from.y + 2; i < 255; i++) {
                    Position pos = Position.fromObject(new Vector3(from.x, i, from.z), from.level);
                    if (pos.getLevelBlock().getId() == Block.AIR) {
                        player.teleport(pos);
                        return new CheckResult("Trying to move into " + block.getName());
                    }
                }
            }

            return new CheckResult("Trying to move into " + block.getName());
        }

        return CheckResult.PASSED;
    }
}
