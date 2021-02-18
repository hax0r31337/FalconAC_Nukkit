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
import me.liuli.falcon.check.combat.VelocityCheck;
import me.liuli.falcon.check.combat.fakePlayer.FakePlayerManager;
import me.liuli.falcon.check.misc.BadPacketsCheck;
import me.liuli.falcon.check.misc.NoSwingCheck;
import me.liuli.falcon.check.movement.FlightCheck;
import me.liuli.falcon.check.movement.SpeedCheck;
import me.liuli.falcon.check.movement.StrafeCheck;
import me.liuli.falcon.check.movement.WaterWalkCheck;
import me.liuli.falcon.check.world.TimerCheck;
import me.liuli.falcon.manager.AnticheatManager;
import me.liuli.falcon.manager.CheckResult;
import me.liuli.falcon.manager.CheckType;
import me.liuli.falcon.manager.PacketBlockManager;

public class PacketListener implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPacketReceive(DataPacketReceiveEvent event) {
        if (event.getPlayer() == null) {
            return;
        }

        Player player = event.getPlayer();
        DataPacket packet = event.getPacket();
        boolean shouldFlag = false;

        if (PacketBlockManager.checkBlock(player)) {
            event.setCancelled(true);
            return;
        }

        if (packet instanceof InventoryTransactionPacket) {
            if (AnticheatManager.canCheckPlayer(player, CheckType.KA_BOT)) {
                CheckResult checkResult = FakePlayerManager.checkBotHurt(event, (InventoryTransactionPacket) packet);
                if (checkResult.failed()) {
                    shouldFlag = AnticheatManager.addVL(player, CheckType.KA_BOT, checkResult);
                }
            }
        }
        if (packet instanceof AnimatePacket) {
            if (AnticheatManager.canCheckPlayer(player, CheckType.NOSWING)) {
                NoSwingCheck.addSwingRecord(player);
            }
        }
        if (packet instanceof MovePlayerPacket) {
            if (AnticheatManager.canCheckPlayer(player, CheckType.TIMER)) {
                CheckResult checkResult = TimerCheck.runCheck(player);
                if (checkResult.failed()) {
                    shouldFlag = AnticheatManager.addVL(player, CheckType.TIMER, checkResult);
                }
            }
            //Nukkit calc onGround and other data by self,i should get these data from packet
            if (player.isAlive()) {
                if (movePlayerHandler((MovePlayerPacket) packet, player)) {
                    event.setCancelled();
                }
            }
        }
        if (!event.isCancelled()) {
            CheckResult checkResult = BadPacketsCheck.runCheck(player, event.getPacket());
            if (checkResult.failed()) {
                shouldFlag = AnticheatManager.addVL(player, CheckType.BADPACKETS, checkResult);
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

        Player player = event.getPlayer();
        if (PacketBlockManager.checkBlock(player)) {
            event.setCancelled(true);
            return;
        }

        CheckCache cache = CheckCache.get(player);
        if (cache == null)
            return;

        DataPacket packet = event.getPacket();
        if (packet instanceof MovePlayerPacket) {
            MovePlayerPacket movePlayerPacket = (MovePlayerPacket) packet;
            if (movePlayerPacket.eid == player.getId()) {
                if (AnticheatManager.canCheckPlayer(player, CheckType.TIMER)) {
                    TimerCheck.compensate(player);
                }
            }
        }
    }

    private boolean movePlayerHandler(MovePlayerPacket packet, Player player) {
        Location from = new Location(player.x, player.y, player.z, player.yaw, player.pitch, player.level);
        Location to = new Location(packet.x, packet.y - 1.62, packet.z, packet.yaw, packet.pitch, player.level);
        Distance distance = new Distance(from, to);
        CheckCache checkCache = CheckCache.get(player);
        if (checkCache == null) {
            return false;
        }
        checkCache.movementCache.handle(player, from, to, distance, packet.onGround);

        if (checkCache.flagDisable) {
            checkCache.flagDisable = false;
            return false;
        }

        double x = distance.getXDifference();
        double z = distance.getZDifference();

        boolean shouldFlag = false;
        if (AnticheatManager.canCheckPlayer(player, CheckType.SPEED)) {
            CheckResult result = SpeedCheck.checkVerticalSpeed(player, distance);
            if (result.failed()) {
                shouldFlag = AnticheatManager.addVL(player, CheckType.SPEED, result);
            }
            result = SpeedCheck.checkXZSpeed(player, x, z, to);
            if (result.failed()) {
                shouldFlag = AnticheatManager.addVL(player, CheckType.SPEED, result);
            }
        }
        if (AnticheatManager.canCheckPlayer(player, CheckType.FLIGHT)) {
            CheckResult result = FlightCheck.runCheck(player, distance);
            if (result.failed()) {
                shouldFlag = AnticheatManager.addVL(player, CheckType.FLIGHT, result);
            }
        }
        if (AnticheatManager.canCheckPlayer(player, CheckType.STRAFE)) {
            CheckResult result = StrafeCheck.runCheck(player, x, z, from, to);
            if (result.failed()) {
                shouldFlag = AnticheatManager.addVL(player, CheckType.STRAFE, result);
            }
        }
        if (AnticheatManager.canCheckPlayer(player, CheckType.WATER_WALK)) {
            CheckResult result = WaterWalkCheck.runCheck(player, packet.onGround);
            if (result.failed()) {
                shouldFlag = AnticheatManager.addVL(player, CheckType.WATER_WALK, result);
            }
        }
        if (AnticheatManager.canCheckPlayer(player, CheckType.VELOCITY)) {
            CheckResult result = VelocityCheck.runCheck(player);
            if (result.failed()) {
                shouldFlag = AnticheatManager.addVL(player, CheckType.VELOCITY, result);
            }
        }

        if (shouldFlag) {
            checkCache.lastPacketFlag = System.currentTimeMillis();
            if (Configuration.flag) {
                //use packet to setback
                checkCache.flagDisable = true;

                MovePlayerPacket movePlayerPacket = new MovePlayerPacket();
                movePlayerPacket.eid = player.getId();
                movePlayerPacket.mode = 2;
                movePlayerPacket.yaw = (float) from.yaw;
                movePlayerPacket.pitch = (float) from.pitch;
                movePlayerPacket.x = (float) from.x;
                movePlayerPacket.y = (float) from.y;
                movePlayerPacket.z = (float) from.z;
                movePlayerPacket.onGround = player.onGround;
                movePlayerPacket.ridingEid = player.getRiding() == null ? 0 : player.getRiding().getId();

                if (!player.getPosition().clone().subtract(0, 1.62, 0).getLevelBlock().canPassThrough()) {
                    checkCache.teleportTime = System.currentTimeMillis();
                    movePlayerPacket.y = (float) from.y + 0.62F;
                }

                player.dataPacket(movePlayerPacket);

                return true;
            }
            return false;
        }
        return false;
    }
}
