package me.liuli.melhor.manage

import cn.nukkit.Player
import cn.nukkit.network.protocol.DataPacket
import me.liuli.melhor.Melhor
import me.liuli.melhor.check.CheckBase
import me.liuli.melhor.check.checks.fight.FightHit1
import java.util.function.Consumer

class CheckManager {
    private val melhor = Melhor.getInstance()

    private val checks = ArrayList<CheckBase>()

    fun registerAll() {
        FightHit1()
    }

    fun registerCheck(check: CheckBase) {
        checks.add(check)
        melhor.logger.warning("Check " + check.name + " registered")
    }

    fun handleUpdate() {
        checks.forEach(Consumer { check: CheckBase -> check.onUpdate() })
    }

    fun handleReceivePacket(player: Player, packet: DataPacket) {
        val data = melhor.playerManager.getCheckData(player) ?: return
        val result = data.update(packet)

        if (result) {
            checks.forEach(Consumer { check: CheckBase -> check.onReceivePacket(data, packet) })
        }
    }

    fun handleSendPacket(player: Player, packet: DataPacket) {
        val data = melhor.playerManager.getCheckData(player) ?: return
        checks.forEach(Consumer { check: CheckBase -> check.onSendPacket(data, packet) })
    }
}