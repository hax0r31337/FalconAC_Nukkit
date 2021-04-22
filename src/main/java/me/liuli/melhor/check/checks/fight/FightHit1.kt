package me.liuli.melhor.check.checks.fight

import cn.nukkit.network.protocol.DataPacket
import me.liuli.melhor.check.CheckBase
import me.liuli.melhor.check.CheckType
import me.liuli.melhor.data.CheckData

class FightHit1 : CheckBase(CheckType.Fight, "Hit", "H1") {
    override fun onReceivePacket(data: CheckData, packet: DataPacket) {
        val entity = data.cacheAttack ?: return

//        val yawDifference = LocationUtil.calculateYawDifference(loc, entity.location)
//        val angleDifference = abs(180 - abs(abs(yawDifference - data.player.yaw) - 180))
//
//        if(angleDifference>getConfig("angleDiff").asDouble) {
//            data.failed(this, false, "Illegal Angle(diff=$angleDifference)", 1)
//        }
    }
}