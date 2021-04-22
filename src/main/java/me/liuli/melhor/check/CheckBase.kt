package me.liuli.melhor.check

import cn.nukkit.network.protocol.DataPacket
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import me.liuli.melhor.Melhor
import me.liuli.melhor.data.CheckData

open class CheckBase(private val checkType: CheckType, private val subCheck: String, private val checkId: String) {
    val name = checkType.name + "[" + checkId + "]"

    private val configObject: JsonObject
    private val melhor = Melhor.getInstance()

    open fun onUpdate() {}
    open fun onReceivePacket(data: CheckData, packet: DataPacket) {}
    open fun onSendPacket(data: CheckData, packet: DataPacket) {}

    init {
        melhor.checkManager.registerCheck(this)
        configObject =
            melhor.acConfig.checksConfig.getAsJsonObject(checkType.configKey).getAsJsonObject(subCheck.toLowerCase())
    }

    fun getConfig(name: String): JsonElement {
        return configObject.get(name)
    }
}