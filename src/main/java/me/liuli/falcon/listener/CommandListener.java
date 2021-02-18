package me.liuli.falcon.listener;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import me.liuli.falcon.cache.CheckCache;
import me.liuli.falcon.cache.Configuration;
import me.liuli.falcon.manager.BanManager;
import me.liuli.falcon.manager.CheckCategory;
import me.liuli.falcon.utils.OtherUtil;

public class CommandListener extends Command {
    private static String versionStr;

    public CommandListener(String version) {
        super("falcon", "Command For FalconAC");
        versionStr = ("Current running &6&lFalcon&bAC &fv&7" + version).replaceAll("&", "§");
    }

    @Override
    public boolean execute(CommandSender sender, String s, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(versionStr);
            if (sender.isOp()) {
                sender.sendMessage("Type /falcon help to show help messages");
            }
            return false;
        }
        if (sender.isOp()) {
            switch (args[0]) {
                case "ban": {
                    Player player = Server.getInstance().getPlayer(args[1]);
                    if (player == null) {
                        sender.sendMessage(Configuration.LANG.ALERT_PREFIX.proc() + "Cannot found player");
                        return false;
                    }
                    long banTime = OtherUtil.getTime() + (Configuration.ban * 60L);
                    if (args.length == 3) {
                        banTime = OtherUtil.getTime() + new Long(args[2]);
                    }
                    BanManager.addBan(player, banTime);
                    break;
                }
                case "unban": {
                    BanManager.removeBan(args[1]);
                    break;
                }
                case "info": {
                    Player player = Server.getInstance().getPlayer(args[1]);
                    if (player != null) {
                        CheckCache cache = CheckCache.get(player);
                        sender.sendMessage(player.getName() + "'s INFO");
                        sender.sendMessage("COMBAT VIOLENCE: " + cache.combatVL + "/" + CheckCategory.COMBAT.vl);
                        sender.sendMessage("MOVEMENT VIOLENCE: " + cache.movementVL + "/" + CheckCategory.MOVEMENT.vl);
                        sender.sendMessage("WORLD VIOLENCE: " + cache.worldVL + "/" + CheckCategory.WORLD.vl);
                        sender.sendMessage("MISC VIOLENCE: " + cache.miscVL + "/" + CheckCategory.MISC.vl);
                    }
                    break;
                }
                case "consoledebug": {
                    Configuration.consoleDebug = !Configuration.consoleDebug;
                    sender.sendMessage("Change \"ConsoleDebug\" to " + Configuration.consoleDebug);
                    sender.sendMessage("This will only change config this time.If you want to change forever,please change in config.yml");
                    break;
                }
                case "playerdebug": {
                    Configuration.playerDebug = !Configuration.playerDebug;
                    sender.sendMessage("Change \"PlayerDebug\" to " + Configuration.playerDebug);
                    sender.sendMessage("This will only change config this time.If you want to change forever,please change in config.yml");
                    break;
                }
                case "flag": {
                    Configuration.flag = !Configuration.flag;
                    sender.sendMessage("Change \"Flag\" to " + Configuration.flag);
                    sender.sendMessage("This will only change config this time.If you want to change forever,please change in config.yml");
                    break;
                }
                case "checkop": {
                    Configuration.checkOp = !Configuration.checkOp;
                    sender.sendMessage("Change \"CheckOp\" to " + Configuration.checkOp);
                    sender.sendMessage("This will only change config this time.If you want to change forever,please change in config.yml");
                    break;
                }
                case "punishboardcast": {
                    Configuration.punishBoardcast = !Configuration.punishBoardcast;
                    sender.sendMessage("Change \"PunishBoardcast\" to " + Configuration.punishBoardcast);
                    sender.sendMessage("This will only change config this time.If you want to change forever,please change in config.yml");
                    break;
                }
                case "help": {
                    sender.sendMessage("§6§lFalcon §bCOMMANDS");
                    sender.sendMessage("/falcon ban <player>");
                    sender.sendMessage("/falcon unban <banid/player>");
                    sender.sendMessage("/falcon info <player>");
                    sender.sendMessage("/falcon consoledebug");
                    sender.sendMessage("/falcon playerdebug");
                    sender.sendMessage("/falcon flag");
                    sender.sendMessage("/falcon checkop");
                    sender.sendMessage("/falcon punishboardcast");
                    break;
                }
                default: {
                    sender.sendMessage(versionStr);
                }
            }
        } else {
            sender.sendMessage(versionStr);
        }
        return false;
    }
}
