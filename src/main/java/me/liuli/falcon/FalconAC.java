package me.liuli.falcon;

import cn.nukkit.permission.Permission;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.plugin.PluginLogger;
import lombok.Getter;
import me.liuli.falcon.listener.PacketListener;
import me.liuli.falcon.manage.CheckManager;
import me.liuli.falcon.other.UpdateTask;
import me.liuli.falcon.utils.OtherUtil;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class FalconAC extends PluginBase {
    @Getter
    private static FalconAC instance;

    @Getter
    private static CheckManager checkManager;
    @Getter
    private static ThreadPoolExecutor threadPoolExecutor;

    @Override
    public void onEnable() {
        instance = this;

        //check lib exists
        if (!this.getServer().getPluginManager().getPlugins().containsKey("FastJSONLib")) {
            //download plugin
            try {
                String pluginPath = this.getServer().getPluginPath();
                OtherUtil.downloadFile("https://hub.fastgit.org/liulihaocai/FJL/releases/download/1.0/FastJSONLib-1.0.jar",
                        pluginPath, "FastJSONLib-1.0.jar");
                //then load it
                this.getServer().getPluginManager()
                        .loadPlugin(new File(pluginPath, "FastJSONLib-1.0.jar").getPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (!new File(this.getDataFolder().getPath() + "/data").exists()) {
            new File(this.getDataFolder().getPath() + "/data").mkdirs();
        }

        //load things
        threadPoolExecutor=new ThreadPoolExecutor(
                Runtime.getRuntime().availableProcessors(),
                Integer.MAX_VALUE,
                60,
                TimeUnit.SECONDS,
                new SynchronousQueue<>(),
                new ThreadPoolExecutor.CallerRunsPolicy());
        checkManager=new CheckManager();
        checkManager.registerAll();

        //add perm
        this.getServer().getPluginManager().addPermission(new Permission("falcon.check","Falcon cheat detection",Permission.DEFAULT_TRUE));
        this.getServer().getPluginManager().addPermission(new Permission("falcon.command","Use /falcon command",Permission.DEFAULT_OP));

        //reg events
        this.getServer().getScheduler().scheduleRepeatingTask(new UpdateTask(checkManager),1,false);
        this.getServer().getPluginManager().registerEvents(new PacketListener(checkManager,threadPoolExecutor),this);

        //done
        this.getLogger().info("§l§6Falcon§bAC §rENABLED!");
        this.getLogger().info("This is an open-source project:https://github.com/liulihaocai/FalconAC");
    }

    @Override
    public void onDisable() {
        threadPoolExecutor.shutdown();
        this.getLogger().info("§l§6Falcon§bAC §rDISABLED!");
    }
}
