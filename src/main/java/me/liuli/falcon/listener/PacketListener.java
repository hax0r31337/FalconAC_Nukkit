package me.liuli.falcon.listener;

import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.server.DataPacketReceiveEvent;
import cn.nukkit.inventory.transaction.data.UseItemOnEntityData;
import cn.nukkit.network.protocol.DataPacket;
import cn.nukkit.network.protocol.InventoryTransactionPacket;
import me.liuli.falcon.cache.CheckCache;
import me.liuli.falcon.check.combat.fakePlayer.FakePlayerManager;
import me.liuli.falcon.manager.AnticheatManager;
import me.liuli.falcon.manager.CheckType;

public class PacketListener implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPacket(DataPacketReceiveEvent event){
        if(event.getPlayer()==null){return;}
        DataPacket packet=event.getPacket();
        if (packet instanceof InventoryTransactionPacket) {
            InventoryTransactionPacket inventoryTransactionPacket=(InventoryTransactionPacket)packet;
            if(AnticheatManager.canCheckPlayer(event.getPlayer(), CheckType.KA_BOT)) {
                long entityId = 0;
                if (inventoryTransactionPacket.transactionType == 3) {
                    UseItemOnEntityData useItemOnEntityData = (UseItemOnEntityData) inventoryTransactionPacket.transactionData;
                    entityId = useItemOnEntityData.entityRuntimeId;
                }
                if (entityId == CheckCache.get(event.getPlayer()).fakePlayer.getEntityId()) {
                    FakePlayerManager.botHurt(event.getPlayer());
                    event.setCancelled();
                }
            }
        }
    }
}
