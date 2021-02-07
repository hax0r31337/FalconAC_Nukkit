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
        CheckCache cache = CheckCache.get(player);
        if (cache == null)
            return CheckResult.PASSED;

        if (player.isSleeping() || player.getRiding() != null || MoveUtils.isNearBlock(player, Block.STILL_WATER) || player.getAllowFlight())
            return CheckResult.PASSED;

        MovementCache movementCache = cache.movementCache;
        double distanceXZ = movementCache.distanceXZ;
        boolean boxedIn = movementCache.topSolid && movementCache.bottomSolid;

        float movementSpeed=player.getMovementSpeed() * 2.5F;

        // AirSpeed
        if (movementCache.airTicks > 1 && movementCache.elytraEffectTicks <= 0 &&
                !MoveUtils.isNearBlock(player,Block.LADDER)) {
            double multiplier = 0.985D;
            double predict = 0.36 * Math.pow(multiplier, movementCache.airTicks + 1);
            // Prevents false when falling from great heights
            if (movementCache.airTicks >= 115)
                predict = Math.max(0.08, predict);
            double limit = CheckType.SPEED.otherData.getJSONObject("airSpeed").getDouble("baseLimit"); // Default 0.03125
            // Adjust for ice
            if (movementCache.iceInfluenceTicks > 0) {
                double iceIncrement = 0.025 * Math.pow(1.038, movementCache.iceInfluenceTicks);
                // Clamp to max value
                if (iceIncrement > 0.18D)
                    iceIncrement = 0.18D;
                if (boxedIn)
                    iceIncrement += 0.45D;
                if (!MoveUtils.couldBeOnBlock(movingTowards,Block.ICE))
                    iceIncrement *= 2.5D;
                predict += iceIncrement;
            }
            // Leniency when boxed in
            if (boxedIn && movementCache.airTicks < 3)
                predict *= 1.2D;
            // Adjust for slime
            if (movementCache.slimeInfluenceTicks > 0) {
                double slimeIncrement = 0.022 * Math.pow(1.0375, movementCache.slimeInfluenceTicks);
                // Clamp to max value
                if (slimeIncrement > 0.12D)
                    slimeIncrement = 0.12D;
                predict += slimeIncrement;
            }
            // Adjust for speed effects
            if (movementCache.hasSpeedEffect)
                predict += player.getEffect(Effect.SPEED).getAmplifier() * 0.05D;
            if (movementCache.hadSpeedEffect && !movementCache.hasSpeedEffect)
                limit *= 1.2D;
            // Adjust for jump boost effects
            if (player.hasEffect(Effect.JUMP))
                predict += player.getEffect(Effect.JUMP).getAmplifier() * 0.05D;
            // Adjust for custom walking speed
            double walkSpeedMultiplier = CheckType.SPEED.otherData.getJSONObject("airSpeed").getDouble("walkSpeedMultiplier");
            // 1.4
            predict += walkSpeedMultiplier * (Math.pow(1.1, ((movementSpeed / 0.20) - 1)) - 1);
            // Slabs sometimes allow for a slight boost after jump
            if (movementCache.halfMovementHistoryCounter > 0)
                predict *= 2.125D;
            if (movementCache.halfMovementHistoryCounter == 0)
                predict *= 1.25D;
            // Strafing in air when nearing terminal velocity gives false positives
            // This fixes the issue but gives hackers some leniency which means we need
            // another check for this
            double deltaMotionY = movementCache.motionY - movementCache.lastMotionY;
            if ((deltaMotionY < 0 && deltaMotionY >= -0.08) || movementCache.airTicks >= 40)
                predict *= (movementCache.airTicks > 60 ? 4.0D : (movementCache.airTicks > 30 ? 3.0D : 2.0D));
            // Players can move faster in air with slow falling
            if (player.hasEffect(Effect.SLOW_FALLING))
                predict *= 1.25D;
            // Fixes false positive when coming out of water
            if (movementCache.nearLiquidTicks >= 7 && movementCache.airTicks >= 14
                    && movementCache.motionY < -0.18 && movementCache.motionY > -0.182)
                predict += Math.abs(movementCache.motionY);

            if (distanceXZ - predict > limit) {
                return new CheckResult("moved too fast in air (speed=" + distanceXZ + ", limit=" + predict
                        + ", box=" + boxedIn + ", at=" + movementCache.airTicks + ")");
            }
        }

        // JumpBehaviour
        if (movementCache.touchedGroundThisTick && !boxedIn && movementCache.slimeInfluenceTicks <= 10) {
            // This happens naturally
            if (movementCache.airTicksBeforeGrounded == movementCache.groundTicks) {
                double minimumDistXZ = CheckType.SPEED.otherData.getDouble("jumpMinimumDistXZ");

                if (distanceXZ >= minimumDistXZ || movementCache.lastDistanceXZ >= minimumDistXZ) {
                    return new CheckResult("had unexpected jumping behaviour (dXZ=" + distanceXZ + ", lXZ="
                                    + movementCache.lastDistanceXZ + ")");
                }
            }
        }

        if (movementCache.groundTicks > 1) {
            double initialLimit = CheckType.SPEED.otherData.getDouble("groundInitialLimit");
            // 0.34
            double limit = initialLimit - 0.0055 * Math.min(9, movementCache.groundTicks);
            // Leniency when moving back on ground
            if (movementCache.groundTicks < 5)
                limit += 0.1D;
            // Slab leniency
            if (movementCache.halfMovementHistoryCounter > 8)
                limit += 0.2D;
            // LivingEntities can give players a small push boost
            limit += 0.2D;
            // Leniency when boxed in
            if (boxedIn)
                limit *= 1.1D;
            // Adjust for speed effects
            if (player.hasEffect(Effect.SPEED))
                limit += player.getEffect(Effect.SPEED).getAmplifier() * 0.06D;
            if (movementCache.hasSpeedEffect && movementCache.groundTicks > 3)
                limit *= 1.4D;
            if (movementCache.hadSpeedEffect && !movementCache.hasSpeedEffect)
                limit *= 1.2D;

            if (movementCache.hasIncreasingEffect && movementCache.groundTicks > 3)
                limit *= 1.12D;
            if (movementCache.hadIncreasingEffect && !movementCache.hasIncreasingEffect)
                limit *= 1.08D;
            if (movementCache.iceInfluenceTicks >= 50) {
                // When moving off ice
                if (!MoveUtils.couldBeOnBlock(movingTowards,Block.ICE))
                    limit *= 2.5D;
                else {
                    // When boxed in and spamming space for boost
                    if (movementCache.topSolid && movementCache.bottomSolid)
                        limit *= 3.0D;
                    else
                        limit *= 1.25D;
                }
            }

            if (MoveUtils.isNearBlock(movingTowards,Block.BED_BLOCK)
                    || MoveUtils.isNearBlock(movingTowards.clone().add(0, -0.5, 0),Block.BED_BLOCK))
                limit *= 2.0D;
            // Adjust for custom walk speed
            limit += (movementSpeed - 0.2) * 2.0D;
            // Prevent NoWeb
            // TODO config
            if (MoveUtils.isNearBlock(player,Block.COBWEB))
                limit *= 0.65D;
            // Sneak speed check
            // TODO config
            if (movementCache.sneakingTicks > 1)
                limit *= 0.68D;

            if (distanceXZ - limit > 0) {
                return new CheckResult("moved too fast on ground (speed=" + distanceXZ + ", limit=" + limit
                        + ", gt=" + movementCache.groundTicks + ")");
            }
        }

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
        double base = (nearBed ? 0.6625 : ((halfMovement) ? 0.7 : 0.52));
        if (fromClimbable)
            base += CheckType.SPEED.otherData.getDouble("verticalCompensation");

        if (player.hasEffect(Effect.JUMP))
            base += player.getEffect(Effect.JUMP).getAmplifier() * 0.2D;
        return base;
    }
}
