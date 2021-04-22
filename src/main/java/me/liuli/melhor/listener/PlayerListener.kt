package me.liuli.melhor.listener

import cn.nukkit.event.EventHandler
import cn.nukkit.event.Listener
import cn.nukkit.event.player.PlayerJoinEvent
import cn.nukkit.event.player.PlayerQuitEvent
import me.liuli.melhor.Melhor

class PlayerListener : Listener {
    private val melhor = Melhor.getInstance()

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        melhor.playerManager.handlePlayerJoin(event.player)
    }

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        melhor.playerManager.handlePlayerLeave(event.player)
    }
}