package me.liuli.falcon.listener;

import cn.nukkit.block.Block;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.*;
import me.liuli.falcon.cache.CheckCache;
import me.liuli.falcon.cache.Configuration;
import me.liuli.falcon.check.combat.AimbotCheck;
import me.liuli.falcon.check.player.IllegalInteractCheck;
import me.liuli.falcon.manager.AnticheatManager;
import me.liuli.falcon.manager.BanManager;
import me.liuli.falcon.manager.CheckResult;
import me.liuli.falcon.manager.CheckType;

public class PlayerListener implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerPreLogin(PlayerPreLoginEvent event){
        BanManager.checkBan(event.getPlayer());
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent event){
        new CheckCache(event.getPlayer());
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerQuit(PlayerQuitEvent event){
        CheckCache.remove(event.getPlayer().getName());
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(PlayerInteractEvent event) {
        boolean shouldFlag=false;
        Block block = event.getBlock();
        if (block != null
                && (event.getAction() == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK || event.getAction() == PlayerInteractEvent.Action.LEFT_CLICK_BLOCK)) {
            if (AnticheatManager.canCheckPlayer(event.getPlayer(), CheckType.ILLEGAL_INTERACT)) {
                CheckResult result = IllegalInteractCheck.performCheck(event.getPlayer(), event);
                if (result.failed()) {
                    shouldFlag=AnticheatManager.addVL(CheckCache.get(event.getPlayer()), CheckType.ILLEGAL_INTERACT,result);
                }
            }
        }
        if(shouldFlag&&Configuration.flag){
            event.setCancelled();
        }
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerMove(PlayerMoveEvent event) {
        boolean shouldFlag=false;
        if (AnticheatManager.canCheckPlayer(event.getPlayer(), CheckType.AIMBOT)) {
            CheckResult result = AimbotCheck.check(event.getPlayer(), event);
            if (result.failed()) {
                shouldFlag=AnticheatManager.addVL(CheckCache.get(event.getPlayer()), CheckType.AIMBOT,result);
            }
        }
        if(shouldFlag&&Configuration.flag){
            event.setCancelled();
        }
    }
}
