package me.liuli.melhor.data

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import me.liuli.melhor.Melhor
import me.liuli.melhor.utils.OtherUtil
import java.io.File

class ACConfig {
    val checksConfig: JsonObject

    val brand: String

    val badPacketsInstantKick: Boolean
    val badPacketsAllowAttackId: Boolean
    val badPacketsAllowAttackSelf: Boolean
    val badPacketsAllowSlot: Boolean
    val badPacketsAllowAirJump: Boolean
    val badPacketsAllowIllegalPitch: Boolean

    init {
        val melhor = Melhor.getInstance()
        val configFile = File(melhor.dataFolder, "config.yml")

        if (!configFile.exists()) {
            OtherUtil.writeFile(configFile, OtherUtil.getTextFromResource("config.yml"))
        }

        val configJson = JsonParser().parse(OtherUtil.y2j(configFile)).asJsonObject

        brand = toColor(configJson.get("brand"))

        checksConfig = configJson.getAsJsonObject("checks")

        //bad packets check
        val badpacketsConfig = configJson.getAsJsonObject("badpackets")
        badPacketsInstantKick = badpacketsConfig.get("instant-kick").asBoolean
        badPacketsAllowAttackId = badpacketsConfig.get("attack-id").asBoolean
        badPacketsAllowAttackSelf = badpacketsConfig.get("attack-self").asBoolean
        badPacketsAllowSlot = badpacketsConfig.get("slot").asBoolean
        badPacketsAllowAirJump = badpacketsConfig.get("air-jump").asBoolean
        badPacketsAllowIllegalPitch = badpacketsConfig.get("illegal-pitch").asBoolean
    }

    private fun toColor(str: JsonElement): String {
        return str.asString.replace("&", "§")
    }
}