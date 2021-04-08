package me.liuli.falcon.check.checks.killaura

import cn.nukkit.network.protocol.DataPacket
import me.liuli.falcon.FalconAC
import me.liuli.falcon.check.CheckBase
import me.liuli.falcon.check.CheckType
import me.liuli.falcon.data.PlayerData

class KAHit1 : CheckBase(CheckType.KillAura, "hit", "HIT1") {
    override fun onSendPacket(data: PlayerData, packet: DataPacket) {
        FalconAC.INSTANCE.logger.info("SEND_PLAYER="+data.player.name+", PACKET="+packet)
    }

    override fun onReceivePacket(data: PlayerData, packet: DataPacket) {
        FalconAC.INSTANCE.logger.info("RECEIVE_PLAYER="+data.player.name+", PACKET="+packet)
    }
}