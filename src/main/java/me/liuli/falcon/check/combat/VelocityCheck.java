package me.liuli.falcon.check.combat;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import me.liuli.falcon.cache.CheckCache;
import me.liuli.falcon.cache.MovementCache;
import me.liuli.falcon.manager.CheckResult;
import me.liuli.falcon.utils.MoveUtil;

public class VelocityCheck {
    public static CheckResult runCheck(Player player){
        CheckCache cache = CheckCache.get(player);
        if (cache == null)
            return CheckResult.PASSED;

        MovementCache movementCache=cache.movementCache;
        if (MoveUtil.isNearBlock(player, Block.STILL_WATER) || player.getAllowFlight() || !player.isAlive() || !movementCache.inVelocity())
            return CheckResult.PASSED;

        return CheckResult.PASSED;
    }
}
