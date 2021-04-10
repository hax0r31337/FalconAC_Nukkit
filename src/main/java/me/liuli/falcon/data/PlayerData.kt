package me.liuli.falcon.data

import cn.nukkit.Player
import cn.nukkit.entity.Entity
import cn.nukkit.inventory.transaction.data.UseItemOnEntityData
import cn.nukkit.network.protocol.DataPacket
import cn.nukkit.network.protocol.InventoryTransactionPacket
import me.liuli.falcon.FalconAC

class PlayerData(val player: Player) {
    val falcon=FalconAC.INSTANCE

    val violence = 0

    var cacheAttack:Entity?=null

    fun update(packet: DataPacket): Boolean{
        //clear cache
        cacheAttack=null
        //update
        if(packet is InventoryTransactionPacket){
            if (packet.transactionType == 3) {
                val entityId = (packet.transactionData as UseItemOnEntityData).entityRuntimeId
                //self attack
                if(entityId==player.id){
                    if(falcon.config.badPacketsInstantKick){
                        falcon.playerManager.kickPlayer(this.player,"Don't suicide...")
                    }
                    return false
                }
                cacheAttack=this.player.level.getEntity(entityId)
                if(cacheAttack==null){
                    if(falcon.config.badPacketsInstantKick){
                        falcon.playerManager.kickPlayer(this.player,"Who are you attacking?")
                    }
                    return false
                }
            }
        }
        return true
    }
}