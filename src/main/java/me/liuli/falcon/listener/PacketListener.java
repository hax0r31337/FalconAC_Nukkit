package me.liuli.falcon.listener;

import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.server.DataPacketReceiveEvent;
import cn.nukkit.network.protocol.AnimatePacket;
import cn.nukkit.network.protocol.DataPacket;
import cn.nukkit.network.protocol.InventoryTransactionPacket;
import me.liuli.falcon.cache.CheckCache;
import me.liuli.falcon.cache.Configuration;
import me.liuli.falcon.check.combat.KillauraCheck;
import me.liuli.falcon.check.combat.fakePlayer.FakePlayerManager;
import me.liuli.falcon.manager.AnticheatManager;
import me.liuli.falcon.manager.CheckResult;
import me.liuli.falcon.manager.CheckType;

public class PacketListener implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPacket(DataPacketReceiveEvent event){
        if(event.getPlayer()==null){return;}
        boolean shouldFlag=false;
        DataPacket packet=event.getPacket();
        if(packet instanceof InventoryTransactionPacket) {
            if(AnticheatManager.canCheckPlayer(event.getPlayer(), CheckType.KA_BOT)) {
                CheckResult checkResult = FakePlayerManager.checkBotHurt(event, (InventoryTransactionPacket) packet);
                if (checkResult.failed()) {
                    shouldFlag = AnticheatManager.addVL(CheckCache.get(event.getPlayer()), CheckType.KA_BOT,checkResult);
                }
            }
        }
        if(packet instanceof AnimatePacket) {
            if (AnticheatManager.canCheckPlayer(event.getPlayer(), CheckType.KA_NOSWING)) {
                KillauraCheck.processSwing(event.getPlayer(),(AnimatePacket)packet);
            }
        }
        if(shouldFlag&&Configuration.flag){
            event.setCancelled();
        }
    }
}
