package me.liuli.falcon.check.movement;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.level.Location;
import cn.nukkit.potion.Effect;
import me.liuli.falcon.cache.CheckCache;
import me.liuli.falcon.cache.Distance;
import me.liuli.falcon.cache.MovementCache;
import me.liuli.falcon.manager.CheckResult;
import me.liuli.falcon.manager.CheckType;
import me.liuli.falcon.other.BlockRelative;
import me.liuli.falcon.utils.MoveUtils;

public class SpeedCheck {
    public static CheckResult checkXZSpeed(Player player, double x, double z, Location movingTowards) {
//        CheckCache cache = CheckCache.get(player);
//        if (cache == null)
//            return CheckResult.PASSED;
//
//        if (player.isSleeping() || player.getRiding() != null || MoveUtils.isNearBlock(player, Block.STILL_WATER) || player.getAllowFlight())
//            return CheckResult.PASSED;
//
//        MovementCache movementCache = cache.movementCache;

        return CheckResult.PASSED;
    }

    public static CheckResult checkVerticalSpeed(Player player, Distance distance) {
        CheckCache cache = CheckCache.get(player);
        if (cache == null)
            return CheckResult.PASSED;

        if (player.isSleeping() || player.getRiding() != null || MoveUtils.isNearBlock(player, Block.STILL_WATER) || player.getAllowFlight())
            return CheckResult.PASSED;

        MovementCache movementCache = cache.movementCache;

        double maxMotionY = getMaxAcceptableMotionY(player, MoveUtils.isNearBlock(distance.getTo(), Block.BED_BLOCK),
                MoveUtils.isNearBlock(BlockRelative.getRelative(BlockRelative.DOWN, distance.getFrom().getLevelBlock()), Block.LADDER),
                movementCache.halfMovement);

        if (movementCache.nearLiquidTicks > 6)
            maxMotionY *= 1.0525D;
        if (movementCache.motionY > maxMotionY && movementCache.slimeInfluenceTicks <= 0) {
            return new CheckResult("exceeded vertical speed limit (mY=" + movementCache.motionY + ", max=" + maxMotionY + ")");
        }

        return CheckResult.PASSED;
    }

    private static double getMaxAcceptableMotionY(Player player, boolean nearBed, boolean fromClimbable, boolean halfMovement) {
        double base = (nearBed ? 0.5625 : ((halfMovement) ? 0.6 : 0.42));
        if (fromClimbable)
            base += CheckType.SPEED.otherData.getDouble("verticalCompensation");

        if (player.hasEffect(Effect.JUMP))
            base += player.getEffect(Effect.JUMP).getAmplifier() * 0.2D;
        return base;
    }
}
