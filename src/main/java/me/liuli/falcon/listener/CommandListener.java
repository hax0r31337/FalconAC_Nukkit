package me.liuli.falcon.listener;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import me.liuli.falcon.cache.Configuration;
import me.liuli.falcon.manager.BanManager;
import me.liuli.falcon.utils.OtherUtils;

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
            return false;
        }
        switch (args[0]) {
            case "ban": {
                Player player = Server.getInstance().getPlayer(args[1]);
                if (player == null) {
                    sender.sendMessage(Configuration.LANG.ALERT_PREFIX.proc() + "Cannot found player");
                    return false;
                }
                long banTime = OtherUtils.getTime() + (Configuration.ban * 60L);
                if (args.length == 3) {
                    banTime = OtherUtils.getTime() + new Long(args[2]);
                }
                BanManager.addBan(player, banTime);
                break;
            }
            case "unban": {
                BanManager.removeBan(args[1]);
                break;
            }
            case "consoledebug": {
                if (sender.isOp()) {
                    Configuration.consoleDebug = !Configuration.consoleDebug;
                    sender.sendMessage("Change \"ConsoleDebug\" to " + Configuration.consoleDebug);
                    sender.sendMessage("This will only change config this time.If you want to change forever,please change in config.yml");
                }
                break;
            }
            case "playerdebug": {
                if (sender.isOp()) {
                    Configuration.playerDebug = !Configuration.playerDebug;
                    sender.sendMessage("Change \"PlayerDebug\" to " + Configuration.playerDebug);
                    sender.sendMessage("This will only change config this time.If you want to change forever,please change in config.yml");
                }
                break;
            }
            case "flag": {
                if (sender.isOp()) {
                    Configuration.flag = !Configuration.flag;
                    sender.sendMessage("Change \"Flag\" to " + Configuration.flag);
                    sender.sendMessage("This will only change config this time.If you want to change forever,please change in config.yml");
                }
                break;
            }
            case "checkop": {
                if (sender.isOp()) {
                    Configuration.checkOp = !Configuration.checkOp;
                    sender.sendMessage("Change \"CheckOp\" to " + Configuration.checkOp);
                    sender.sendMessage("This will only change config this time.If you want to change forever,please change in config.yml");
                }
                break;
            }
            case "punishboardcast": {
                if (sender.isOp()) {
                    Configuration.punishBoardcast = !Configuration.punishBoardcast;
                    sender.sendMessage("Change \"PunishBoardcast\" to " + Configuration.punishBoardcast);
                    sender.sendMessage("This will only change config this time.If you want to change forever,please change in config.yml");
                }
                break;
            }
            case "help": {
                if (sender.isOp()) {
                    sender.sendMessage("§6§lFalcon§bAC §fCOMMANDS");
                    sender.sendMessage("/falcon consoledebug");
                    sender.sendMessage("/falcon playerdebug");
                    sender.sendMessage("/falcon flag");
                    sender.sendMessage("/falcon checkop");
                    sender.sendMessage("/falcon punishboardcast");
                }
                break;
            }
            default: {
                sender.sendMessage(versionStr);
                break;
            }
        }
        return false;
    }
}
