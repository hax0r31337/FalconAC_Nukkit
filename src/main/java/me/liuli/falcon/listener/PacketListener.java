package me.liuli.falcon.listener;

import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.server.DataPacketReceiveEvent;
import cn.nukkit.event.server.DataPacketSendEvent;
import cn.nukkit.network.protocol.AnimatePacket;
import cn.nukkit.network.protocol.DataPacket;
import cn.nukkit.network.protocol.InventoryTransactionPacket;
import cn.nukkit.network.protocol.MovePlayerPacket;
import me.liuli.falcon.cache.Configuration;
import me.liuli.falcon.check.combat.fakePlayer.FakePlayerManager;
import me.liuli.falcon.check.misc.BadPacketsCheck;
import me.liuli.falcon.check.misc.NoSwingCheck;
import me.liuli.falcon.check.world.TimerCheck;
import me.liuli.falcon.manager.AnticheatManager;
import me.liuli.falcon.manager.CheckResult;
import me.liuli.falcon.manager.CheckType;

public class PacketListener implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPacketReceive(DataPacketReceiveEvent event) {
        if (event.getPlayer() == null) {
            return;
        }
        boolean shouldFlag = false;
        DataPacket packet = event.getPacket();
        if (packet instanceof InventoryTransactionPacket) {
            if (AnticheatManager.canCheckPlayer(event.getPlayer(), CheckType.KA_BOT)) {
                CheckResult checkResult = FakePlayerManager.checkBotHurt(event, (InventoryTransactionPacket) packet);
                if (checkResult.failed()) {
                    shouldFlag = AnticheatManager.addVL(event.getPlayer(), CheckType.KA_BOT, checkResult);
                }
            }
        }
        if (packet instanceof AnimatePacket) {
            if (AnticheatManager.canCheckPlayer(event.getPlayer(), CheckType.NOSWING)) {
                NoSwingCheck.addSwingRecord(event.getPlayer());
            }
        }
        if (packet instanceof MovePlayerPacket) {
            if (AnticheatManager.canCheckPlayer(event.getPlayer(), CheckType.TIMER)) {
                CheckResult checkResult = TimerCheck.runCheck(event.getPlayer());
                if (checkResult.failed()) {
                    shouldFlag = AnticheatManager.addVL(event.getPlayer(), CheckType.TIMER, checkResult);
                }
            }
        }
        if (!event.isCancelled()) {
            CheckResult checkResult = BadPacketsCheck.runCheck(event.getPlayer(), event.getPacket());
            if (checkResult.failed()) {
                shouldFlag = AnticheatManager.addVL(event.getPlayer(), CheckType.BADPACKETS, checkResult);
            }
        }
        if (shouldFlag && Configuration.flag) {
            event.setCancelled();
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPacketSend(DataPacketSendEvent event) {
        if (event.getPlayer() == null) {
            return;
        }
        DataPacket packet = event.getPacket();
        if (packet instanceof MovePlayerPacket) {
            if (AnticheatManager.canCheckPlayer(event.getPlayer(), CheckType.TIMER)) {
                TimerCheck.compensate(event.getPlayer());
            }
        }
    }
}
