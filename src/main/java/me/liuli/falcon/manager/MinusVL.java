package me.liuli.falcon.manager;

import cn.nukkit.Player;
import cn.nukkit.Server;
import me.liuli.falcon.cache.CheckCache;

import java.util.Map;
import java.util.UUID;

public class MinusVL implements Runnable {
    public boolean running = true;

    @Override
    public void run() {
        while (running) {
            try {
                Thread.sleep(1000);
                for (Map.Entry<UUID, Player> entry : Server.getInstance().getOnlinePlayers().entrySet()) {
                    CheckCache checkCache = CheckCache.get(entry.getValue());
                    if (checkCache == null) {
                        continue;
                    }
                    if (checkCache.combatVL > 0) {
                        checkCache.combatVL -= CheckCategory.COMBAT.minusVl;
                    }
                    if (checkCache.movementVL > 0) {
                        checkCache.movementVL -= CheckCategory.MOVEMENT.minusVl;
                    }
                    if (checkCache.worldVL > 0) {
                        checkCache.worldVL -= CheckCategory.WORLD.minusVl;
                    }
                    if (checkCache.miscVL > 0) {
                        checkCache.miscVL -= CheckCategory.MISC.minusVl;
                    }
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }
}
