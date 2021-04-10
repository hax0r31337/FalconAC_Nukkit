package me.liuli.falcon

import cn.nukkit.permission.Permission
import cn.nukkit.plugin.PluginBase
import com.google.gson.Gson
import com.google.gson.JsonParser
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

    init {
        INSTANCE=this
    }

    val threadPoolExecutor = ThreadPoolExecutor(
        Runtime.getRuntime().availableProcessors(), Int.MAX_VALUE,
        60,
        TimeUnit.SECONDS,
        SynchronousQueue(),
        CallerRunsPolicy()
    )

    lateinit var checkManager: CheckManager
    lateinit var playerManager: PlayerManager
    lateinit var config: ACConfig

    val gson = Gson()
    val jsonParser = JsonParser()

    override fun onEnable() {
        val folder = this.dataFolder
        if (!folder.exists()) {
            folder.mkdirs()
        }

        config=ACConfig()
        checkManager=CheckManager()
        playerManager= PlayerManager()

        //init
        checkManager.registerAll()

        //perms
        this.server.pluginManager.addPermission(Permission("falcon.check", "falcon cheat detection", Permission.DEFAULT_TRUE))
        this.server.pluginManager.addPermission(Permission("falcon.command", "Use /falcon command", Permission.DEFAULT_OP))

        //reg events
        this.server.scheduler.scheduleRepeatingTask(UpdateTask(checkManager), 1, false)
        this.server.pluginManager.registerEvents(PacketListener(), this)
        this.server.pluginManager.registerEvents(PlayerListener(), this)

        this.logger.info("§l§6Falcon§bAC §rENABLED!");
        this.logger.info("This is an open-source project:https://github.com/liulihaocai/FalconAC");
    }

    override fun onDisable() {
        threadPoolExecutor.shutdown()
        logger.info("§l§6Falcon§bAC §rDISABLED!")
    }
}