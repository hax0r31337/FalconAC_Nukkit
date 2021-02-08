package me.liuli.falcon.listener;

import cn.nukkit.block.Block;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.entity.EntityMotionEvent;
import cn.nukkit.event.player.*;
import cn.nukkit.level.Location;
import me.liuli.falcon.cache.CheckCache;
import me.liuli.falcon.cache.Configuration;
import me.liuli.falcon.cache.Distance;
import me.liuli.falcon.check.combat.AimbotCheck;
import me.liuli.falcon.check.movement.NoClipCheck;
import me.liuli.falcon.check.movement.SpeedCheck;
import me.liuli.falcon.check.movement.StrafeCheck;
import me.liuli.falcon.check.movement.WaterWalkCheck;
import me.liuli.falcon.check.world.IllegalInteractCheck;
import me.liuli.falcon.manager.*;

public class PlayerListener implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerPreLogin(PlayerPreLoginEvent event) {
        BanManager.checkBan(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        new CheckCache(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerQuit(PlayerQuitEvent event) {
        CheckCache.remove(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(PlayerInteractEvent event) {
        boolean shouldFlag = false;
        Block block = event.getBlock();
        if (block != null
                && (event.getAction() == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK || event.getAction() == PlayerInteractEvent.Action.LEFT_CLICK_BLOCK)) {
            if (AnticheatManager.canCheckPlayer(event.getPlayer(), CheckType.ILLEGAL_INTERACT)) {
                CheckResult result = IllegalInteractCheck.isValidTarget(event.getPlayer(), event.getBlock());
                if (result.failed()) {
                    shouldFlag = AnticheatManager.addVL(event.getPlayer(), CheckType.ILLEGAL_INTERACT, result);
                }
                result = IllegalInteractCheck.rayTraceCheck(event.getPlayer(), event.getBlock());
                if (result.failed()) {
                    shouldFlag = AnticheatManager.addVL(event.getPlayer(), CheckType.ILLEGAL_INTERACT, result);
                }
            }
        }
        if (shouldFlag && Configuration.flag) {
            event.setCancelled();
        } else {
            AnticheatManager.minusPassVl(event.getPlayer(), CheckCategory.WORLD);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerMove(PlayerMoveEvent event) {
        if (event == null) {
            return;
        }
        Location from = event.getFrom();
        Location to = event.getTo();
        Distance distance = new Distance(from, to);
        CheckCache.get(event.getPlayer()).movementCache.handle(event.getPlayer(), from, to, distance);

        double x = distance.getXDifference();
        double z = distance.getZDifference();

        boolean shouldFlag = false;
        if (AnticheatManager.canCheckPlayer(event.getPlayer(), CheckType.AIMBOT)) {
            CheckResult result = AimbotCheck.check(event.getPlayer(), event);
            if (result.failed()) {
                shouldFlag = AnticheatManager.addVL(event.getPlayer(), CheckType.AIMBOT, result);
            }
        }
        if (AnticheatManager.canCheckPlayer(event.getPlayer(), CheckType.NOCLIP)) {
            CheckResult result = NoClipCheck.check(event.getPlayer(), event);
            if (result.failed()) {
                shouldFlag = AnticheatManager.addVL(event.getPlayer(), CheckType.NOCLIP, result);
            }
        }
        if (AnticheatManager.canCheckPlayer(event.getPlayer(), CheckType.SPEED)) {
            CheckResult result = SpeedCheck.checkVerticalSpeed(event.getPlayer(), distance);
            if (result.failed()) {
                shouldFlag = AnticheatManager.addVL(event.getPlayer(), CheckType.SPEED, result);
            }
            result = SpeedCheck.checkXZSpeed(event.getPlayer(), x,z,event.getTo());
            if (result.failed()) {
                shouldFlag = AnticheatManager.addVL(event.getPlayer(), CheckType.SPEED, result);
            }
        }
        if (AnticheatManager.canCheckPlayer(event.getPlayer(), CheckType.STRAFE)) {
            CheckResult result = StrafeCheck.runCheck(event.getPlayer(), x,z, event.getFrom(), event.getTo());
            if (result.failed()) {
                shouldFlag = AnticheatManager.addVL(event.getPlayer(), CheckType.STRAFE, result);
            }
        }
        if (AnticheatManager.canCheckPlayer(event.getPlayer(), CheckType.WATER_WALK)) {
            CheckResult result = WaterWalkCheck.runCheck(event.getPlayer(), x, distance.getYDifference(), z);
            if (result.failed()) {
                shouldFlag = AnticheatManager.addVL(event.getPlayer(), CheckType.WATER_WALK, result);
            }
        }
        if (shouldFlag && Configuration.flag) {
            event.setCancelled();
        } else {
            AnticheatManager.minusPassVl(event.getPlayer(), CheckCategory.MOVEMENT);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        CheckCache cache = CheckCache.get(event.getPlayer());
        cache.lastTPTime = System.currentTimeMillis();
    }
}
