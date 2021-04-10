package me.liuli.falcon.manage

import cn.nukkit.Player
import cn.nukkit.Server
import cn.nukkit.network.protocol.DisconnectPacket
import cn.nukkit.scheduler.AsyncTask
import cn.nukkit.scheduler.Task
import me.liuli.falcon.FalconAC
import me.liuli.falcon.data.PlayerData
import java.util.UUID
import kotlin.collections.HashMap

class PlayerManager {
    private val playerDataMap=HashMap<UUID,PlayerData>()
    private val kickList=ArrayList<Player>()

    fun handlePlayerJoin(player: Player){
        playerDataMap[player.uniqueId] = PlayerData(player)
    }

    fun handlePlayerLeave(player: Player){
        playerDataMap.remove(player.uniqueId)
        kickList.remove(player)
    }

    fun getPlayerData(player: Player): PlayerData? {
        return getPlayerData(player.uniqueId?:return null)
    }

    fun getPlayerData(uuid: UUID): PlayerData? {
        return playerDataMap[uuid]
    }

    fun canHandlePacket(player: Player): Boolean{
        return !kickList.contains(player)
    }

    fun kickPlayer(player: Player,reason: String){
        kickList.add(player)

        Server.getInstance().scheduler.scheduleDelayedTask(object: Task(){
            override fun onRun(p0: Int) {
                val packet=DisconnectPacket()
                packet.hideDisconnectionScreen=false
                packet.message=reason+"\n\n"+FalconAC.INSTANCE.config.brand
                player.dataPacket(packet)
                Thread.sleep(300)
                player.kick(reason,false)
            }
        },20,true)
    }
}