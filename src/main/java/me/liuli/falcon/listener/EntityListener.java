package me.liuli.falcon.listener;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import me.liuli.falcon.cache.CheckCache;
import me.liuli.falcon.check.combat.fakePlayer.FakePlayerManager;
import me.liuli.falcon.manager.AnticheatManager;
import me.liuli.falcon.manager.CheckType;

public class EntityListener implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event){
        if(event.getEntity() instanceof Player){
            Player player=(Player) event.getEntity();
            if(AnticheatManager.canCheckPlayer(player, CheckType.KA_BOT)) {
                CheckCache.get(player).fakePlayer.doSwing(player);
            }
        }
        if(event.getDamager() instanceof Player){
            Player player=(Player) event.getDamager();
            if(AnticheatManager.canCheckPlayer(player, CheckType.KA_BOT)) {
                CheckCache.get(player).fakePlayer.showDamage(player);
                FakePlayerManager.playerHurt(player);
            }
        }
    }
}
