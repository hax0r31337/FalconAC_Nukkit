package me.liuli.falcon;

import cn.nukkit.plugin.PluginBase;
import me.liuli.falcon.cache.Configuration;
import me.liuli.falcon.listener.*;
import me.liuli.falcon.manager.BanManager;
import me.liuli.falcon.manager.MinusVL;

import java.io.File;

public class FalconAC extends PluginBase {
    public static FalconAC plugin;
    public static int CONFIG_VERSION = 4;
    private static MinusVL minusVLThread;

    @Override
    public void onEnable() {
        plugin = this;

        //load libs
        if (!new File(FalconAC.plugin.getDataFolder().getPath() + "/data").exists()) {
            new File(FalconAC.plugin.getDataFolder().getPath() + "/data").mkdirs();
        }

        //load config
        Configuration.loadConfig();
        BanManager.loadBanData();

        //start threads
        minusVLThread = new MinusVL();
        new Thread(minusVLThread).start();

        //register listeners
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        getServer().getPluginManager().registerEvents(new EntityListener(), this);
        getServer().getPluginManager().registerEvents(new BlockListener(), this);
        getServer().getPluginManager().registerEvents(new PacketListener(), this);

        //register command
        plugin.getServer().getCommandMap().register("falcon", new CommandListener(plugin.getDescription().getVersion()));

        //done
        plugin.getLogger().info("§l§6Falcon§bAC §rLOADED!");
        plugin.getLogger().info("This is an open-source project:https://github.com/liulihaocai/FalconAC");
    }

    @Override
    public void onDisable() {
        minusVLThread.running=false;
        BanManager.saveBanData();
    }
}
