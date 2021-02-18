package me.liuli.falcon.listener;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.*;
import me.liuli.falcon.cache.CheckCache;
import me.liuli.falcon.cache.Configuration;
import me.liuli.falcon.check.combat.AimbotCheck;
import me.liuli.falcon.check.movement.NoClipCheck;
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
            }
        }
        if (shouldFlag) {
            if (Configuration.flag) {
                event.setCancelled();
            }
            return;
        }
        AnticheatManager.minusPassVl(event.getPlayer(), CheckCategory.WORLD);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerMove(PlayerMoveEvent event) {
        if (event == null) {
            return;
        }

        Player player = event.getPlayer();
        CheckCache checkCache = CheckCache.get(player);
        if (checkCache == null) {
            return;
        }

        boolean shouldFlag = false;

        if (AnticheatManager.canCheckPlayer(player, CheckType.AIMBOT)) {
            CheckResult result = AimbotCheck.check(player, event.getFrom(), event.getTo());
            if (result.failed()) {
                shouldFlag = AnticheatManager.addVL(player, CheckType.AIMBOT, result);
            }
        }
        if (AnticheatManager.canCheckPlayer(player, CheckType.NOCLIP)) {
            CheckResult result = NoClipCheck.check(player, event.getFrom());
            if (result.failed()) {
                shouldFlag = AnticheatManager.addVL(player, CheckType.NOCLIP, result);
            }
        }

        if (shouldFlag && Configuration.flag) {
            event.setCancelled();
        }

        if ((System.currentTimeMillis() - checkCache.lastPacketFlag) > 100) {
            AnticheatManager.minusPassVl(player, CheckCategory.MOVEMENT);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerTP(PlayerTeleportEvent event) {
        CheckCache checkCache = CheckCache.get(event.getPlayer());
        if (checkCache == null) {
            return;
        }
        checkCache.teleportTime = System.currentTimeMillis();
    }
}
