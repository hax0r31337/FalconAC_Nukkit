package me.liuli.falcon.manager;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.network.protocol.DisconnectPacket;
import me.liuli.falcon.FalconAC;
import me.liuli.falcon.api.FalconPlayerHackEvent;
import me.liuli.falcon.api.FalconPlayerPunishEvent;
import me.liuli.falcon.cache.CheckCache;
import me.liuli.falcon.cache.Configuration;
import me.liuli.falcon.utils.OtherUtil;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

public class AnticheatManager {
    public static boolean addVL(Player player, CheckType checkType, CheckResult result) {
        FalconPlayerHackEvent event = new FalconPlayerHackEvent(player, checkType, result, true);
        Server.getInstance().getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return false;
        }

        CheckCache cache = CheckCache.get(player);
        float nowVL = -1, maxVL = -1;
        boolean shouldFlag = false;
        switch (event.checkType.category) {
            case COMBAT: {
                cache.combatVL += event.vl;
                if (cache.combatVL > CheckCategory.COMBAT.warnVl) {
                    warnPlayer(cache);
                }
                if (cache.combatVL > CheckCategory.COMBAT.vl) {
                    punishPlayer(cache, checkType.category);
                }
                if (cache.combatVL > CheckCategory.COMBAT.flagVl) {
                    shouldFlag = true;
                }
                nowVL = cache.combatVL;
                maxVL = CheckCategory.COMBAT.vl;
                break;
            }
            case MOVEMENT: {
                cache.movementVL += event.vl;
                if (cache.movementVL > CheckCategory.MOVEMENT.warnVl) {
                    warnPlayer(cache);
                }
                if (cache.movementVL > CheckCategory.MOVEMENT.vl) {
                    punishPlayer(cache, checkType.category);
                }
                if (cache.movementVL > CheckCategory.MOVEMENT.flagVl) {
                    shouldFlag = true;
                }
                nowVL = cache.movementVL;
                maxVL = CheckCategory.MOVEMENT.vl;
                break;
            }
            case WORLD: {
                cache.worldVL += event.vl;
                if (cache.worldVL > CheckCategory.WORLD.warnVl) {
                    warnPlayer(cache);
                }
                if (cache.worldVL > CheckCategory.WORLD.vl) {
                    punishPlayer(cache, checkType.category);
                }
                if (cache.worldVL > CheckCategory.WORLD.flagVl) {
                    shouldFlag = true;
                }
                nowVL = cache.worldVL;
                maxVL = CheckCategory.WORLD.vl;
                break;
            }
            case MISC: {
                cache.miscVL += event.vl;
                if (cache.miscVL > CheckCategory.MISC.warnVl) {
                    warnPlayer(cache);
                }
                if (cache.miscVL > CheckCategory.MISC.vl) {
                    punishPlayer(cache, checkType.category);
                }
                if (cache.miscVL > CheckCategory.MISC.flagVl) {
                    shouldFlag = true;
                }
                nowVL = cache.miscVL;
                maxVL = CheckCategory.MISC.vl;
                break;
            }
        }
        String debugMsg = result.message + " (tps=" + Server.getInstance().getTicksPerSecond() + ",ping=" + player.getPing() + ")";
        if (Configuration.playerDebug) {
            cache.player.sendMessage(Configuration.LANG.DEBUG.proc(new String[]{cache.player.getName(), checkType.category.name() + "." + checkType.name(), String.valueOf(nowVL), String.valueOf(maxVL), debugMsg}));
        }
        if (Configuration.consoleDebug) {
            FalconAC.plugin.getLogger().info(cache.player.getName() + " §7failed §b" + checkType.category.name() + "." + checkType.name() + " §fvl:" + nowVL + "/" + maxVL + " " + debugMsg);
        }
        return (shouldFlag && !checkType.canSmartFlag && event.flag);
    }

    public static void minusPassVl(Player player, CheckCategory category) {
        CheckCache cache = CheckCache.get(player);
        switch (category) {
            case COMBAT: {
                if ((cache.combatVL - category.passMinus) >= 0) {
                    cache.combatVL -= category.passMinus;
                }
                break;
            }
            case MOVEMENT: {
                if ((cache.movementVL - category.passMinus) >= 0) {
                    cache.movementVL -= category.passMinus;
                }
                break;
            }
            case WORLD: {
                if ((cache.worldVL - category.passMinus) >= 0) {
                    cache.worldVL -= category.passMinus;
                }
                break;
            }
            case MISC: {
                if ((cache.miscVL - category.passMinus) >= 0) {
                    cache.miscVL -= category.passMinus;
                }
                break;
            }
        }
    }

    public static void warnPlayer(CheckCache cache) {
        if (!cache.warn) {
            cache.warn = true;
            cache.player.sendMessage(Configuration.LANG.ALERT_PREFIX.proc() + Configuration.LANG.WARNING.proc());
        }
    }

    public static void boardcastMessage(String str) {
        for (Map.Entry<UUID, Player> entry : Server.getInstance().getOnlinePlayers().entrySet()) {
            entry.getValue().sendMessage(Configuration.LANG.ALERT_PREFIX.proc() + str);
        }
        FalconAC.plugin.getLogger().info(str);
    }

    public static void punishPlayer(CheckCache cache, CheckCategory category) {
        PacketBlockManager.addBlock(cache.player);

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                PacketBlockManager.removeBlock(cache.player);

                FalconPlayerPunishEvent event = new FalconPlayerPunishEvent(cache.player, category.result);
                Server.getInstance().getPluginManager().callEvent(event);
                if (event.isCancelled()) {
                    return;
                }

                switch (event.result) {
                    case KICK: {
                        if (Configuration.punishBoardcast) {
                            boardcastMessage(Configuration.LANG.KICK.proc(new String[]{cache.player.getName()}));
                        }
                        kick(cache.player, Configuration.LANG.KICK_REASON.proc());
                        break;
                    }
                    case BAN: {
                        if (Configuration.punishBoardcast) {
                            boardcastMessage(Configuration.LANG.BAN.proc(new String[]{cache.player.getName()}));
                        }

                        long banTime;
                        if (Configuration.ban == -1) {
                            banTime = -1;
                        } else {
                            banTime = OtherUtil.getTime() + (Configuration.ban * 60L);
                        }
                        BanManager.addBan(cache.player, banTime);
                    }
                }
            }
        }, Configuration.punishDelay);
    }

    public static boolean canCheckPlayer(Player player, CheckType checkType) {
        boolean isOp = true;
        if (player.isOp() && !Configuration.checkOp) {
            isOp = false;
        }
        return checkType.enable && isOp;
    }

    public static void kick(Player player, String message) {
        DisconnectPacket disconnectPacket = new DisconnectPacket();
        disconnectPacket.hideDisconnectionScreen = false;
        disconnectPacket.message = message;
        player.dataPacket(disconnectPacket);

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                player.kick(message, false);
            }
        }, 1000);
    }
}
