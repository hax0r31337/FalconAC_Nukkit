package me.liuli.falcon.check.world;

import cn.nukkit.Player;
import me.liuli.falcon.cache.CheckCache;
import me.liuli.falcon.manager.CheckResult;
import me.liuli.falcon.manager.CheckType;

public class FastPlaceCheck {
    public static CheckResult check(Player player) {
        int minimumTime = CheckType.FAST_PLACE.otherData.getInteger("minimumTime");
        CheckCache cache = CheckCache.get(player);

        int thisTime = (int) (System.currentTimeMillis() - cache.lastPlace);
        cache.lastPlace = System.currentTimeMillis();
        if (thisTime < minimumTime) {
            return new CheckResult("tried to place a block " + thisTime + " ms after the last one (min=" + minimumTime + " ms)");
        }
        return CheckResult.PASSED;
    }
}
