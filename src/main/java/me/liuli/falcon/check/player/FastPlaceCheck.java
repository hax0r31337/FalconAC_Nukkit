package me.liuli.falcon.check.player;

import cn.nukkit.Player;
import me.liuli.falcon.manager.CheckResult;
import me.liuli.falcon.manager.CheckType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FastPlaceCheck {
    private static Map<UUID,Long> lastBlockPlaceTime=new HashMap<>();
    public static CheckResult check(Player player){
        int minimumTime=CheckType.FAST_PLACE.otherData.getInteger("minimumTime");
        UUID playerUUID=player.getUniqueId();
        if(!lastBlockPlaceTime.containsKey(playerUUID)){
            lastBlockPlaceTime.put(playerUUID,0L);
        }
        int thisTime= (int) (System.currentTimeMillis()-lastBlockPlaceTime.get(playerUUID));
        lastBlockPlaceTime.put(playerUUID,System.currentTimeMillis());
        if(thisTime<minimumTime){
            return new CheckResult("tried to place a block " + thisTime + " ms after the last one (min=" + minimumTime + " ms)");
        }
        return CheckResult.PASSED;
    }
}
