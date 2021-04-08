package me.liuli.falcon.listener

import cn.nukkit.event.EventHandler
import me.liuli.falcon.manage.CheckManager
import java.util.concurrent.ThreadPoolExecutor
import cn.nukkit.event.EventPriority
import cn.nukkit.event.Listener
import cn.nukkit.event.server.DataPacketReceiveEvent
import cn.nukkit.event.server.DataPacketSendEvent

class PacketListener(private val checkManager: CheckManager, private val threadPoolExecutor: ThreadPoolExecutor) :
    Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    fun onPacketReceive(event: DataPacketReceiveEvent) {
        if(event.player.hasPermission("falcon.check")){
            // player have perm to check
            threadPoolExecutor.execute { checkManager.handleReceivePacket(event.player, event.packet) }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onPacketSend(event: DataPacketSendEvent) {
        if(event.player.hasPermission("falcon.check")) {
            threadPoolExecutor.execute { checkManager.handleSendPacket(event.player, event.packet) }
        }
    }
}