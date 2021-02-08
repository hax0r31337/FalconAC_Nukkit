package me.liuli.falcon.listener;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.server.DataPacketReceiveEvent;
import cn.nukkit.event.server.DataPacketSendEvent;
import cn.nukkit.level.Location;
import cn.nukkit.network.protocol.AnimatePacket;
import cn.nukkit.network.protocol.DataPacket;
import cn.nukkit.network.protocol.InventoryTransactionPacket;
import cn.nukkit.network.protocol.MovePlayerPacket;
import me.liuli.falcon.cache.CheckCache;
import me.liuli.falcon.cache.Configuration;
import me.liuli.falcon.cache.Distance;
import me.liuli.falcon.check.combat.AimbotCheck;
import me.liuli.falcon.check.combat.fakePlayer.FakePlayerManager;
import me.liuli.falcon.check.misc.BadPacketsCheck;
import me.liuli.falcon.check.misc.NoSwingCheck;
import me.liuli.falcon.check.movement.NoClipCheck;
import me.liuli.falcon.check.movement.SpeedCheck;
import me.liuli.falcon.check.movement.StrafeCheck;
import me.liuli.falcon.check.movement.WaterWalkCheck;
import me.liuli.falcon.check.world.TimerCheck;
import me.liuli.falcon.manager.AnticheatManager;
import me.liuli.falcon.manager.CheckCategory;
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
            //Nukkit calc onGround and other data by self,i should get these data from packet
            if(movePlayerHandler((MovePlayerPacket) packet, event.getPlayer())){
                event.setCancelled();
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

    private boolean movePlayerHandler(MovePlayerPacket packet, Player player){
        Location from = new Location(player.x,player.y,player.z, player.yaw,player.pitch,player.level);
        Location to = new Location(packet.x, packet.y-1.62, packet.z,packet.yaw,packet.pitch,player.level);
        Distance distance = new Distance(from, to);
        CheckCache checkCache=CheckCache.get(player);
        if(checkCache==null) {
            return false;
        }
        checkCache.movementCache.handle(player, from, to, distance, packet.onGround);

        double x = distance.getXDifference();
        double z = distance.getZDifference();

        boolean shouldFlag = false;
        if (AnticheatManager.canCheckPlayer(player, CheckType.NOCLIP)) {
            CheckResult result = NoClipCheck.check(player, from);
            if (result.failed()) {
                shouldFlag = AnticheatManager.addVL(player, CheckType.NOCLIP, result);
            }
        }
        if (AnticheatManager.canCheckPlayer(player, CheckType.SPEED)) {
            CheckResult result = SpeedCheck.checkVerticalSpeed(player, distance);
            if (result.failed()) {
                shouldFlag = AnticheatManager.addVL(player, CheckType.SPEED, result);
            }
            result = SpeedCheck.checkXZSpeed(player,x,z,to);
            if (result.failed()) {
                shouldFlag = AnticheatManager.addVL(player, CheckType.SPEED, result);
            }
        }
        if (AnticheatManager.canCheckPlayer(player, CheckType.STRAFE)) {
            CheckResult result = StrafeCheck.runCheck(player, x,z, from,to);
            if (result.failed()) {
                shouldFlag = AnticheatManager.addVL(player, CheckType.STRAFE, result);
            }
        }
        if (AnticheatManager.canCheckPlayer(player, CheckType.WATER_WALK)) {
            CheckResult result = WaterWalkCheck.runCheck(player, x, distance.getYDifference(), z);
            if (result.failed()) {
                shouldFlag = AnticheatManager.addVL(player, CheckType.WATER_WALK, result);
            }
        }
        if (shouldFlag) {
            if(Configuration.flag) {
                player.teleport(from);
                return true;
            }
            return false;
        }
        AnticheatManager.minusPassVl(player, CheckCategory.MOVEMENT);
        return false;
    }
}
