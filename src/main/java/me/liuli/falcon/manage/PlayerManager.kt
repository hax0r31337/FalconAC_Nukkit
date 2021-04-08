package me.liuli.falcon.manage

import cn.nukkit.Player
import me.liuli.falcon.data.PlayerData
import java.util.UUID
import kotlin.collections.HashMap

class PlayerManager {
    private val playerDataMap=HashMap<UUID,PlayerData>()

    fun handlePlayerJoin(player: Player){
        playerDataMap[player.uniqueId] = PlayerData(player)
    }

    fun handlePlayerLeave(player: Player){
        playerDataMap.remove(player.uniqueId)
    }

    fun getPlayerData(player: Player): PlayerData? {
        return getPlayerData(player.uniqueId)
    }

    fun getPlayerData(uuid: UUID): PlayerData? {
        return playerDataMap[uuid]
    }
}