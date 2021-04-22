package me.liuli.melhor;

import cn.nukkit.permission.Permission;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.plugin.PluginManager;
import me.liuli.melhor.data.ACConfig;
import me.liuli.melhor.listener.PacketListener;
import me.liuli.melhor.listener.PlayerListener;
import me.liuli.melhor.manage.CheckManager;
import me.liuli.melhor.manage.PlayerManager;
import me.liuli.melhor.other.UpdateTask;

import java.io.File;

public class Melhor extends PluginBase {
    private static Melhor instance;

    private ACConfig config;
    private CheckManager checkManager;
    private PlayerManager playerManager;

    public static Melhor getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
    }

    @Override
    public void onDisable() {
        this.getLogger().info("§l§6M§belhor §rDISABLED!");
        File folder = this.getDataFolder();
        if (!folder.exists()) {
            folder.mkdirs();
        }

        config = new ACConfig();
        checkManager = new CheckManager();
        playerManager = new PlayerManager();

        //init
        checkManager.registerAll();

        //perms
        PluginManager pm = this.getServer().getPluginManager();
        pm.addPermission(new Permission("melhor.check", "melhor cheat detection", Permission.DEFAULT_TRUE));
        pm.addPermission(new Permission("melhor.command", "Use /melhor command", Permission.DEFAULT_OP));

        //reg events
        this.getServer().getScheduler().scheduleRepeatingTask(new UpdateTask(checkManager), 1, false);
        pm.registerEvents(new PacketListener(), this);
        pm.registerEvents(new PlayerListener(), this);

        this.getLogger().info("§l§6M§belhor §rENABLED!");
        this.getLogger().info("This is an open-source project:https://github.com/Cookie-Studio/Melhor");
    }

    public ACConfig getACConfig() {
        return config;
    }

    public CheckManager getCheckManager() {
        return checkManager;
    }

    public PlayerManager getPlayerManager() {
        return playerManager;
    }
}
