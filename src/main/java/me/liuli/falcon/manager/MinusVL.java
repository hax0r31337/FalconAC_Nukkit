package me.liuli.falcon.manager;

import cn.nukkit.Player;
import cn.nukkit.Server;
import me.liuli.falcon.cache.CheckCache;

import java.util.Map;
import java.util.UUID;

public class MinusVL implements Runnable {
    @Override
    public void run() {
        while (true){
            try {
                Thread.sleep(1000);
                for(Map.Entry<UUID, Player> entry: Server.getInstance().getOnlinePlayers().entrySet()){
                    CheckCache checkCache=CheckCache.get(entry.getValue());
                    if(checkCache==null){
                        continue;
                    }
                    if(checkCache.CombatVL>0){
                        checkCache.CombatVL-=CheckCategory.COMBAT.minusVl;
                    }
                    if(checkCache.MovementVL>0){
                        checkCache.MovementVL-=CheckCategory.MOVEMENT.minusVl;
                    }
                    if(checkCache.WorldVL>0){
                        checkCache.WorldVL-=CheckCategory.WORLD.minusVl;
                    }
                    if(checkCache.MiscVL>0){
                        checkCache.MiscVL-=CheckCategory.MISC.minusVl;
                    }
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }
}
