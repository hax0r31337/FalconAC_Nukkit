package me.liuli.falcon.manage

import me.liuli.falcon.check.CheckBase
import me.liuli.falcon.check.checks.fight.FightHit1
import me.liuli.falcon.FalconAC
import cn.nukkit.Player
import cn.nukkit.network.protocol.DataPacket
import java.util.ArrayList
import java.util.function.Consumer

class CheckManager {
    private val checks = ArrayList<CheckBase>()
    fun registerAll() {
        FightHit1()
    }

    fun registerCheck(check: CheckBase) {
        checks.add(check)
        FalconAC.INSTANCE.logger.warning("Check " + check.name + " registered")
    }

    fun handleUpdate() {
        checks.forEach(Consumer { check: CheckBase -> check.onUpdate() })
    }

    fun handleReceivePacket(player: Player, packet: DataPacket) {
        val data=FalconAC.INSTANCE.playerManager.getPlayerData(player) ?: return
        val result=data.update(packet)
        val loc=player.location

        if(result) {
            checks.forEach(Consumer { check: CheckBase -> check.onReceivePacket(data, loc, packet) })
        }
    }

    fun handleSendPacket(player: Player, packet: DataPacket) {
        val data=FalconAC.INSTANCE.playerManager.getPlayerData(player) ?: return
        checks.forEach(Consumer { check: CheckBase -> check.onSendPacket(data, packet) })
    }
}