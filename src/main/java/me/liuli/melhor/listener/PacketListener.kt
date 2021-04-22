package me.liuli.melhor.listener

import cn.nukkit.event.EventHandler
import cn.nukkit.event.EventPriority
import cn.nukkit.event.Listener
import cn.nukkit.event.server.DataPacketReceiveEvent
import cn.nukkit.event.server.DataPacketSendEvent
import me.liuli.melhor.Melhor

class PacketListener : Listener {
    private val melhor = Melhor.getInstance()

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onPacketReceive(event: DataPacketReceiveEvent) {
        val player = event.player ?: return

        if (!melhor.playerManager.canHandlePacket(player)) {
            event.setCancelled()
            return
        }

        if (event.player.hasPermission("melhor.check")) {
            // player have perm to check
            melhor.checkManager.handleReceivePacket(event.player, event.packet)
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onPacketSend(event: DataPacketSendEvent) {
        val player = event.player ?: return

        if (!melhor.playerManager.canHandlePacket(player)) {
            return
        }

        if (event.player.hasPermission("melhor.check")) {
            melhor.checkManager.handleSendPacket(event.player, event.packet)
        }
    }
}