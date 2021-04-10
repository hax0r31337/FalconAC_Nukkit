package me.liuli.falcon.data

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import me.liuli.falcon.FalconAC
import me.liuli.falcon.utils.OtherUtil
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
        val falcon=FalconAC.INSTANCE
        val configFile=File(FalconAC.INSTANCE.dataFolder,"config.yml")

        if(!configFile.exists()){
            OtherUtil.writeFile(configFile,OtherUtil.getTextFromResource("config.yml"))
        }

        val configJson=falcon.jsonParser.parse(OtherUtil.y2j(configFile)).asJsonObject

        brand=toColor(configJson.get("brand"))

        checksConfig=configJson.getAsJsonObject("checks")

        //bad packets check
        val badpacketsConfig=configJson.getAsJsonObject("badpackets")
        badPacketsInstantKick=badpacketsConfig.get("instant-kick").asBoolean
        badPacketsAllowAttackId=badpacketsConfig.get("attack-id").asBoolean
        badPacketsAllowAttackSelf=badpacketsConfig.get("attack-self").asBoolean
        badPacketsAllowSlot=badpacketsConfig.get("slot").asBoolean
        badPacketsAllowAirJump=badpacketsConfig.get("air-jump").asBoolean
        badPacketsAllowIllegalPitch=badpacketsConfig.get("illegal-pitch").asBoolean
    }

    private fun toColor(str: JsonElement):String{
        return str.asString.replace("&","§")
    }
}