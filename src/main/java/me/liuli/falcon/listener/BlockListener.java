package me.liuli.falcon.listener;

import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockBreakEvent;
import me.liuli.falcon.cache.CheckCache;
import me.liuli.falcon.cache.Configuration;
import me.liuli.falcon.check.player.IllegalInteractCheck;
import me.liuli.falcon.manager.AnticheatManager;
import me.liuli.falcon.manager.CheckResult;
import me.liuli.falcon.manager.CheckType;

public class BlockListener implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockPlace(BlockBreakEvent event){
        boolean shouldFlag=false;
        if (AnticheatManager.canCheckPlayer(event.getPlayer(), CheckType.ILLEGAL_INTERACT)) {
            CheckResult result = IllegalInteractCheck.performCheck(event.getPlayer(), event);
            if (result.failed()) {
                shouldFlag=AnticheatManager.addVL(CheckCache.get(event.getPlayer()), CheckType.ILLEGAL_INTERACT);
            }
        }
        if(shouldFlag&& Configuration.flag){
            event.setCancelled();
        }
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent event){
        boolean shouldFlag=false;
        if (AnticheatManager.canCheckPlayer(event.getPlayer(), CheckType.ILLEGAL_INTERACT)) {
            CheckResult result = IllegalInteractCheck.performCheck(event.getPlayer(), event);
            if (result.failed()) {
                shouldFlag=AnticheatManager.addVL(CheckCache.get(event.getPlayer()), CheckType.ILLEGAL_INTERACT);
            }
        }
        if(shouldFlag&&Configuration.flag){
            event.setCancelled();
        }
    }
}
