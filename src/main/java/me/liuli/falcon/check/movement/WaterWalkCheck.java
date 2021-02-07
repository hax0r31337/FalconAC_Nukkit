package me.liuli.falcon.check.movement;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.potion.Effect;
import me.liuli.falcon.cache.CheckCache;
import me.liuli.falcon.cache.MovementCache;
import me.liuli.falcon.manager.CheckResult;
import me.liuli.falcon.manager.CheckType;
import me.liuli.falcon.utils.MoveUtils;

public class WaterWalkCheck {
    public static CheckResult runCheck(Player player, double x, double y, double z) {
        CheckCache cache = CheckCache.get(player);
        if (cache == null)
            return CheckResult.PASSED;

        MovementCache movementCache = cache.movementCache;
        if (movementCache.distanceXZ <= 0 || player.getRiding() != null || MoveUtils.inBlock(player, Block.LILY_PAD)
                || player.isSwimming() || player.getAllowFlight()
                || movementCache.topSolid || movementCache.bottomSolid)
            return CheckResult.PASSED;

        Block blockBeneath = player.getPosition().clone().subtract(0, 0.1, 0).getLevelBlock();
        if (!(MoveUtils.isLiquid(blockBeneath) || MoveUtils.isSurroundedByBlock(player.getPosition(), Block.STILL_WATER))) {
            return CheckResult.PASSED;
        }

        if (((movementCache.motionY == 0 && movementCache.lastMotionY == 0)
                || movementCache.motionY == MoveUtils.JUMP_MOTION_Y)
                && movementCache.distanceXZ > CheckType.WATER_WALK.otherData.getDouble("walkMinimumDistXZ"))
            return new CheckResult("tried to walk on water (xz=" + movementCache.distanceXZ + ")");

        double minAbsMotionY = 0.12D;
        if (player.hasEffect(Effect.SPEED))
            minAbsMotionY += player.getEffect(Effect.SPEED).getAmplifier() * 0.05D;
        if (Math.abs(movementCache.lastMotionY - movementCache.motionY) > minAbsMotionY
                && movementCache.distanceXZ > CheckType.WATER_WALK.otherData.getDouble("lungeMinimumDistXZ")
                && movementCache.lastMotionY > -0.25)
            return new CheckResult("tried to lunge in water (xz="
                    + movementCache.distanceXZ + ", absMotionY="
                    + Math.abs(movementCache.lastMotionY - movementCache.motionY) + ")");

        return CheckResult.PASSED;
    }
}
