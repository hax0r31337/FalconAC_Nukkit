package me.liuli.melhor.data

import cn.nukkit.Player
import cn.nukkit.entity.Entity
import cn.nukkit.inventory.transaction.data.UseItemOnEntityData
import cn.nukkit.network.protocol.DataPacket
import cn.nukkit.network.protocol.InventoryTransactionPacket
import me.liuli.melhor.Melhor
import me.liuli.melhor.check.CheckBase

class CheckData(player: Player) {
    val player: Player = player
    var violence: Int = 0

    private val melhor = Melhor.getInstance()

    var cacheAttack: Entity? = null

    fun update(packet: DataPacket): Boolean {
        //clear cache
        cacheAttack = null
        //update
        if (packet is InventoryTransactionPacket) {
            if (packet.transactionType == 3) {
                val entityId = (packet.transactionData as UseItemOnEntityData).entityRuntimeId
                //self attack
                if (entityId == player.id) {
                    if (melhor.acConfig.badPacketsInstantKick) {
                        melhor.playerManager.kickPlayer(this.player, "Don't suicide...")
                    }
                    return false
                }
                cacheAttack = this.player.level.getEntity(entityId)
                if (cacheAttack == null) {
                    if (melhor.acConfig.badPacketsInstantKick) {
                        melhor.playerManager.kickPlayer(this.player, "Who are you attacking?")
                    }
                    return false
                }
            }
        }
        return true
    }

    fun failed(check: CheckBase, tpBack: Boolean, msg: String, vl: Int) {
        val violenceAdd = (check.getConfig("baseVL").asInt) * vl
        violence += violenceAdd

//        player.sendMessage("FAILED $check.name vl+$violenceAdd $msg")
    }
}
