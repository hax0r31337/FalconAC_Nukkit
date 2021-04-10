package me.liuli.falcon.check

import cn.nukkit.Player
import cn.nukkit.level.Location
import cn.nukkit.level.Position
import cn.nukkit.network.protocol.DataPacket
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import me.liuli.falcon.FalconAC
import me.liuli.falcon.data.PlayerData

open class CheckBase(private val checkType: CheckType, private val subCheck: String, private val checkId: String) {
    val name = checkType.name + "[" + checkId + "]"

    private val configObject: JsonObject

    open fun onUpdate() {}
    open fun onReceivePacket(data: PlayerData, loc: Location, packet: DataPacket) {}
    open fun onSendPacket(data: PlayerData, packet: DataPacket) {}

    init {
        FalconAC.INSTANCE.checkManager.registerCheck(this)
        configObject=FalconAC.INSTANCE.config.checksConfig.getAsJsonObject(checkType.configKey).getAsJsonObject(subCheck.toLowerCase())
    }

    fun getConfig(name: String): JsonElement{
        return configObject.get(name)
    }

    fun failed(player: Player,tpBack: Boolean,msg: String,vl: Int){
        val violenceAdd=getConfig("baseVL").asInt
        player.sendMessage("FAILED $name vl+$violenceAdd $msg")
    }
}