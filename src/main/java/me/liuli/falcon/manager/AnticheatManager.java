package me.liuli.falcon.manager;

import cn.nukkit.Player;
import cn.nukkit.Server;
import me.liuli.falcon.FalconAC;
import me.liuli.falcon.cache.CheckCache;
import me.liuli.falcon.cache.Configuration;
import me.liuli.falcon.utils.OtherUtils;

import java.util.Map;
import java.util.UUID;

public class AnticheatManager {
    public static boolean addVL(Player player, CheckType checkType, CheckResult result) {
        CheckCache cache = CheckCache.get(player);
        int nowVL = -1, maxVL = -1;
        boolean shouldFlag = false;
        switch (checkType.category) {
            case COMBAT: {
                cache.combatVL += checkType.addVl;
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
                cache.movementVL += checkType.addVl;
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
                cache.worldVL += checkType.addVl;
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
                cache.miscVL += checkType.addVl;
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
        if (Configuration.playerDebug) {
            cache.player.sendMessage(Configuration.LANG.DEBUG.proc(new String[]{cache.player.getName(), checkType.category.name() + "." + checkType.name(), String.valueOf(nowVL), String.valueOf(maxVL), result.message}));
        }
        if (Configuration.consoleDebug) {
            FalconAC.plugin.getLogger().info(cache.player.getName() + " §7failed §b" + checkType.category.name() + "." + checkType.name() + " §fvl:" + nowVL + "/" + maxVL + " " + result.message);
        }
        return shouldFlag;
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
        switch (category.result) {
            case KICK: {
                if (Configuration.punishBoardcast) {
                    boardcastMessage(Configuration.LANG.KICK.proc(new String[]{cache.player.getName()}));
                }
                cache.player.kick(Configuration.LANG.KICK_REASON.proc(), false);
                break;
            }
            case BAN: {
                if (Configuration.punishBoardcast) {
                    boardcastMessage(Configuration.LANG.BAN.proc(new String[]{cache.player.getName()}));
                }
                BanManager.addBan(cache.player, OtherUtils.getTime() + (Configuration.ban * 60L));
            }
        }
    }

    public static boolean canCheckPlayer(Player player, CheckType checkType) {
        boolean isOp = true;
        if (player.isOp() && !Configuration.checkOp) {
            isOp = false;
        }
        return checkType.enable && isOp;
    }
}
