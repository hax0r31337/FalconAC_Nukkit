package me.liuli.falcon.listener

import cn.nukkit.event.EventHandler
import cn.nukkit.event.Listener
import cn.nukkit.event.player.PlayerJoinEvent
import cn.nukkit.event.player.PlayerQuitEvent
import me.liuli.falcon.FalconAC

class PlayerListener : Listener {
    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent){
        FalconAC.INSTANCE.playerManager.handlePlayerJoin(event.player)
    }

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent){
        FalconAC.INSTANCE.playerManager.handlePlayerLeave(event.player)
    }
}