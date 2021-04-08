package me.liuli.falcon

import cn.nukkit.permission.Permission
import cn.nukkit.plugin.PluginBase
import me.liuli.falcon.data.ACConfig
import me.liuli.falcon.listener.PacketListener
import me.liuli.falcon.listener.PlayerListener
import me.liuli.falcon.manage.CheckManager
import me.liuli.falcon.manage.PlayerManager
import me.liuli.falcon.other.UpdateTask
import java.util.concurrent.SynchronousQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy
import java.util.concurrent.TimeUnit

class FalconAC : PluginBase() {
    companion object{
        @JvmStatic
        lateinit var INSTANCE: FalconAC
    }

    val threadPoolExecutor = ThreadPoolExecutor(
        Runtime.getRuntime().availableProcessors(), Int.MAX_VALUE,
        60,
        TimeUnit.SECONDS,
        SynchronousQueue(),
        CallerRunsPolicy()
    )

    val checkManager = CheckManager()
    val playerManager = PlayerManager()
    val config = ACConfig()

    override fun onEnable() {
        INSTANCE=this

        val folder = this.dataFolder
        if (!folder.exists()) {
            folder.mkdirs()
        }

        //init
        checkManager.registerAll()

        //perms
        this.server.pluginManager.addPermission(Permission("falcon.check", "falcon cheat detection", Permission.DEFAULT_TRUE))
        this.server.pluginManager.addPermission(Permission("falcon.command", "Use /falcon command", Permission.DEFAULT_OP))

        //reg events
        this.server.scheduler.scheduleRepeatingTask(UpdateTask(checkManager), 1, false)
        this.server.pluginManager.registerEvents(PacketListener(checkManager, threadPoolExecutor), this)
        this.server.pluginManager.registerEvents(PlayerListener(), this)

        this.logger.info("§l§6Falcon§bAC §rENABLED!");
        this.logger.info("This is an open-source project:https://github.com/liulihaocai/FalconAC");
    }

    override fun onDisable() {
        threadPoolExecutor.shutdown()
        logger.info("§l§6Falcon§bAC §rDISABLED!")
    }
}