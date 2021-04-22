package me.liuli.melhor.manage

import cn.nukkit.Player
import cn.nukkit.Server
import cn.nukkit.network.protocol.DisconnectPacket
import cn.nukkit.scheduler.Task
import me.liuli.melhor.Melhor
import me.liuli.melhor.data.CheckData
import java.util.*

class PlayerManager {
    private val melhor = Melhor.getInstance()

    private val checkDataMap = HashMap<UUID, CheckData>()
    private val kickList = ArrayList<Player>()

    fun handlePlayerJoin(player: Player) {
        checkDataMap[player.uniqueId] = CheckData(player)
    }

    fun handlePlayerLeave(player: Player) {
        checkDataMap.remove(player.uniqueId)
        kickList.remove(player)
    }

    fun getCheckData(player: Player): CheckData? {
        return getCheckData(player.uniqueId ?: return null)
    }

    fun getCheckData(uuid: UUID): CheckData? {
        return checkDataMap[uuid]
    }

    fun canHandlePacket(player: Player): Boolean {
        return !kickList.contains(player)
    }

    fun kickPlayer(player: Player, reason: String) {
        kickList.add(player)

        Server.getInstance().scheduler.scheduleDelayedTask(object : Task() {
            override fun onRun(p0: Int) {
                val packet = DisconnectPacket()
                packet.hideDisconnectionScreen = false
                packet.message = reason + "\n\n" + melhor.acConfig.brand
                player.dataPacket(packet)
                Thread.sleep(300)
                player.kick(reason, false)
            }
        }, 20, true)
    }
}