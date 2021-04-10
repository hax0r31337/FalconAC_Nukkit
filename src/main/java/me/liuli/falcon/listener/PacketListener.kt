package me.liuli.falcon.listener

import cn.nukkit.event.EventHandler
import me.liuli.falcon.manage.CheckManager
import java.util.concurrent.ThreadPoolExecutor
import cn.nukkit.event.EventPriority
import cn.nukkit.event.Listener
import cn.nukkit.event.server.DataPacketReceiveEvent
import cn.nukkit.event.server.DataPacketSendEvent
import me.liuli.falcon.FalconAC

class PacketListener : Listener {
    private val falcon=FalconAC.INSTANCE

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onPacketReceive(event: DataPacketReceiveEvent) {
        val player=event.player ?: return

        if(!falcon.playerManager.canHandlePacket(player)){
            event.setCancelled()
            return
        }

        if(event.player.hasPermission("falcon.check")){
            // player have perm to check
            falcon.threadPoolExecutor.execute { falcon.checkManager.handleReceivePacket(event.player, event.packet) }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onPacketSend(event: DataPacketSendEvent) {
        val player=event.player ?: return

        if(!falcon.playerManager.canHandlePacket(player)){
            return
        }

        if(event.player.hasPermission("falcon.check")) {
            falcon.threadPoolExecutor.execute { falcon.checkManager.handleSendPacket(event.player, event.packet) }
        }
    }
}