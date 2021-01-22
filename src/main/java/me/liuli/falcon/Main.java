package me.liuli.falcon;

import cn.nukkit.plugin.PluginBase;
import me.liuli.falcon.cache.Configuration;
import me.liuli.falcon.listener.BlockListener;
import me.liuli.falcon.listener.EntityListener;
import me.liuli.falcon.listener.PacketListener;
import me.liuli.falcon.listener.PlayerListener;
import me.liuli.falcon.manager.BanManager;
import me.liuli.falcon.manager.FalconCommand;
import me.liuli.falcon.manager.MinusVL;
import me.liuli.falcon.utils.OtherUtils;

import java.io.File;

public class Main extends PluginBase {
    public static Main plugin;
    public static String jarDir=Main.class.getProtectionDomain().getCodeSource().getLocation().getFile();
    public static int CONFIG_VERSION=1;
    private static Thread minusVLThread;
    @Override
    public void onEnable() {
        plugin=this;

        //load libs
        if (!new File(Main.plugin.getDataFolder().getPath()+"/data").exists()) {
            new File(Main.plugin.getDataFolder().getPath()+"/data").mkdirs();
        }
        if (!new File(Main.plugin.getDataFolder().getPath()+"/fastjson.jar").exists()) {
            OtherUtils.readJar("fastjson.jar",Main.jarDir,Main.plugin.getDataFolder().getPath()+"/fastjson.jar");
        }
        OtherUtils.injectClass(new File(Main.plugin.getDataFolder().getPath()+"/fastjson.jar"));

        //load config
        Configuration.loadConfig();
        BanManager.loadBanData();

        //start threads
        minusVLThread=new Thread(new MinusVL());
        minusVLThread.start();

        //register listeners
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        getServer().getPluginManager().registerEvents(new EntityListener(), this);
        getServer().getPluginManager().registerEvents(new BlockListener(), this);
        getServer().getPluginManager().registerEvents(new PacketListener(), this);

        //register command
        plugin.getServer().getCommandMap().register("falcon",new FalconCommand(plugin.getDescription().getVersion()));

        //done
        plugin.getLogger().info("§l§6Falcon§bAC §rLOADED!");
    }
    @Override
    public void onDisable() {
    }
}
