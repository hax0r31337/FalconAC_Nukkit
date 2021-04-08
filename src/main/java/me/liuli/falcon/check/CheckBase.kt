package me.liuli.falcon.check

import cn.nukkit.network.protocol.DataPacket
import me.liuli.falcon.FalconAC
import me.liuli.falcon.data.PlayerData

open class CheckBase(private val checkType: CheckType, private val subCheck: String, private val checkId: String) {
    val name: String
        get() = checkType.name + "[" + checkId + "]"

    open fun onUpdate() {}
    open fun onReceivePacket(data: PlayerData, packet: DataPacket) {}
    open fun onSendPacket(data: PlayerData, packet: DataPacket) {}

    init {
        FalconAC.INSTANCE.checkManager.registerCheck(this)
    }
}