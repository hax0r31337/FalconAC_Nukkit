package me.liuli.falcon.check.player;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.event.Event;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.block.BlockPlaceEvent;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.math.Vector3;
import me.liuli.falcon.check.combat.KillauraCheck;
import me.liuli.falcon.manager.CheckResult;
import me.liuli.falcon.manager.CheckType;

public class IllegalInteractCheck {
    public static CheckResult performCheck(Player player, Event event) {
        if (event instanceof BlockPlaceEvent) {
            return checkBlockPlace(player, (BlockPlaceEvent) event);
        } else if (event instanceof BlockBreakEvent) {
            return checkBlockBreak(player, (BlockBreakEvent) event);
        } else if (event instanceof PlayerInteractEvent) {
            return checkInteract(player, (PlayerInteractEvent) event);
        }
        return CheckResult.PASSED;
    }
    private static CheckResult checkInteract(Player player, PlayerInteractEvent event) {
        if (!isValidTarget(player, event.getBlock())) {
            return CheckResult.FAILED;
        }
        return CheckResult.PASSED;
    }
    private static CheckResult checkBlockBreak(Player player, BlockBreakEvent event) {
        if (!isValidTarget(player, event.getBlock())) {
            return CheckResult.FAILED;
        }
        return CheckResult.PASSED;
    }
    private static CheckResult checkBlockPlace(Player player, BlockPlaceEvent event) {
        if (event.getBlock().isSolid() && !isValidTarget(player, event.getBlock())) {
            return CheckResult.FAILED;
        }
        return CheckResult.PASSED;
    }
    private static boolean isValidTarget(Player player, Block block) {
        double distance = player.getLocation().distance(new Vector3(block.x,block.y,block.z));
        double maxDistance = player.gamemode == 1
                ? CheckType.ILLEGAL_INTERACT.otherData.getDouble("creativeRange")
                : CheckType.ILLEGAL_INTERACT.otherData.getDouble("survivalRange");
        maxDistance += player.getPing()
                * CheckType.ILLEGAL_INTERACT.otherData.getDouble("pingCompensation");
        if (distance < maxDistance) {
            return true;
        }
        double yawDifference = KillauraCheck.calculateYawDifference(player.getLocation(), block.getLocation());
        double angleDifference = Math.abs(180 - Math.abs(Math.abs(yawDifference - player.yaw) - 180));
        if(block.y==player.getFloorY()-1||block.y==player.getFloorY()+2) {
            return Math.round(angleDifference) <= CheckType.ILLEGAL_INTERACT.otherData.getInteger("angle");
        }
        return false;
    }
}
