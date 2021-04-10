package me.liuli.falcon.check.checks.fight

import cn.nukkit.level.Location
import cn.nukkit.level.Position
import cn.nukkit.network.protocol.DataPacket
import me.liuli.falcon.check.CheckBase
import me.liuli.falcon.check.CheckType
import me.liuli.falcon.data.PlayerData
import me.liuli.falcon.utils.LocationUtil
import kotlin.math.abs

class FightHit1 : CheckBase(CheckType.Fight, "hit", "H1") {
    override fun onReceivePacket(data: PlayerData, loc: Location, packet: DataPacket) {
        val entity=data.cacheAttack ?: return

        val yawDifference = LocationUtil.calculateYawDifference(loc, entity.location)
        val angleDifference = abs(180 - abs(abs(yawDifference - data.player.yaw) - 180))

        if(angleDifference>getConfig("angleDiff").asDouble) {
            failed(data.player, false, "Illegal Angle(diff=$angleDifference)", 1)
        }
    }
}