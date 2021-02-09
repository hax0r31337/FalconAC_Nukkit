package me.liuli.falcon.check.movement;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.potion.Effect;
import me.liuli.falcon.cache.CheckCache;
import me.liuli.falcon.cache.Distance;
import me.liuli.falcon.cache.MovementCache;
import me.liuli.falcon.manager.CheckResult;
import me.liuli.falcon.manager.CheckType;
import me.liuli.falcon.utils.MoveUtils;

public class FlightCheck {
    public static CheckResult runCheck(Player player, Distance distance) {
        CheckCache cache = CheckCache.get(player);
        if (cache == null)
            return CheckResult.PASSED;

        if (cache.inVelocity() || player.isSleeping() || player.getRiding() != null || player.getAllowFlight())
            return CheckResult.PASSED;

        MovementCache movementCache = cache.movementCache;
        if (movementCache.nearLiquidTicks > 0 || movementCache.halfMovement || MoveUtils.isNearBlock(player.getPosition(),Block.LADDER))
            return CheckResult.PASSED;

        int minAirTicks = 13;
        if (player.hasEffect(Effect.JUMP))
            minAirTicks += player.getEffect(Effect.JUMP).getAmplifier() * 3;

        if (movementCache.halfMovementHistoryCounter > 25)
            minAirTicks += 5;

        //AirFlight
        if (movementCache.airTicks > minAirTicks && movementCache.elytraEffectTicks <= 25) {
            int blockPlaceAccountingTime = (int) (0.25 * (Math.min(player.getPing(), 1000)));
            long lastPlacedBlock = cache.lastPlace;
            double maxMotionY = System.currentTimeMillis() - lastPlacedBlock > blockPlaceAccountingTime ? 0 : 0.42;
            // Fixes snow false positive
            if (movementCache.motionY < 0.004)
                maxMotionY += 0.004D;
            if (MoveUtils.isNearBlock(player.getPosition(),Block.STILL_WATER)
                    || MoveUtils.isNearBlock(distance.getFrom().clone().subtract(0, 0.51, 0),Block.STILL_WATER))
                maxMotionY += 0.05;
            if (movementCache.motionY > maxMotionY && movementCache.slimeInfluenceTicks <= 0
                    && !MoveUtils.isNearBlock(distance.getTo().clone().subtract(0, 1.25, 0),Block.LADDER)
                    && !MoveUtils.isNearBlock(distance.getTo().clone().subtract(0, 0.75, 0),Block.LADDER)
                    && (!MoveUtils.isNearBlock(distance.getTo().clone().subtract(0, 1.5, 0),Block.STILL_WATER)
                    && !distance.getTo().clone().subtract(0, 0.5, 0).getLevelBlock().canPassThrough()))
                return new CheckResult("tried to fly on the Y-axis (mY=" + movementCache.motionY + ", max=" + maxMotionY + ")");

            if (Math.abs(movementCache.motionY
                    - movementCache.lastMotionY) < (movementCache.airTicks >= 115 ? 1E-3 : 5E-3)
                    && !player.hasEffect(Effect.SLOW_FALLING) && !MoveUtils.isNearBlock(player.getPosition(),Block.COBWEB)
                    && movementCache.elytraEffectTicks <= 25
                    && !MoveUtils.isNearBlock(distance.getFrom().clone().subtract(0, 0.51D, 0),Block.LADDER)
                    && !MoveUtils.isNearBlock(player.getPosition(),Block.STILL_WATER)
                    && !MoveUtils.isNearBlock(distance.getFrom().clone().subtract(0, 0.51D, 0),Block.STILL_WATER))
                return new CheckResult("had too little Y dropoff (diff="
                        + Math.abs(movementCache.motionY - movementCache.lastMotionY) + ")");
        }

        //AirClimb
        if (movementCache.lastMotionY > 0 && movementCache.motionY > 0 && movementCache.airTicks == 2
                && Math.round(movementCache.lastMotionY * 1000) != 420
                && Math.round(movementCache.motionY * 1000) != 248
                && !(Math.round(movementCache.motionY * 1000) == 333
                && Math.round(movementCache.lastMotionY * 1000) != 333)
                && !player.hasEffect(Effect.JUMP)
                && (!MoveUtils.isNearBlock(distance.getTo(),Block.BED_BLOCK) || ((MoveUtils.isNearBlock(distance.getTo(),Block.BED_BLOCK)
                || MoveUtils.isNearBlock(distance.getTo().clone().add(0, -0.51, 0),Block.BED_BLOCK))
                && movementCache.motionY > 0.15))
                && movementCache.slimeInfluenceTicks == 0 && movementCache.elytraEffectTicks <= 25)
            return new CheckResult("tried to climb air (mY=" + movementCache.motionY + ")");

        if (movementCache.motionY > 0.42 && movementCache.airTicks > 2
                && !player.hasEffect(Effect.JUMP)
                && !(Math.round(movementCache.motionY * 1000) == 425 && movementCache.airTicks == 11)
                && movementCache.slimeInfluenceTicks == 0 && movementCache.elytraEffectTicks <= 25) {
            return new CheckResult("tried to climb air (mY=" + movementCache.motionY + ", at=" + movementCache.airTicks + ")");
        }

        if (movementCache.airTicks >= minAirTicks
                && movementCache.lastMotionY < 0 && movementCache.motionY > 0
                && movementCache.elytraEffectTicks <= 25) {
            return new CheckResult("tried to climb air (mY=" + movementCache.motionY + ", at=" + movementCache.airTicks + ")");
        }

        //GroundFlight
        if((!player.onGround) && movementCache.onGround){
            return new CheckResult("faked ground to fly (mY=" + movementCache.motionY + ", gt=" + movementCache.groundTicks + ")");
        }

        //Gravity
        if (!movementCache.onGround && movementCache.motionY < 0
                && !MoveUtils.isNearBlock(player.getPosition(),Block.COBWEB) && movementCache.elytraEffectTicks <= 25
                && !player.hasEffect(Effect.SLOW_FALLING)) {
            double gravitatedY = (movementCache.lastMotionY - 0.08) * CheckType.FLIGHT.otherData.getJSONObject("gravity").getDouble("friction");
            double offset = Math.abs(gravitatedY - movementCache.motionY);
            double maxOffset = CheckType.FLIGHT.otherData.getJSONObject("gravity").getDouble("maxOffset");
            if (MoveUtils.isNearBlock(distance.getFrom().clone().subtract(0, 0.51D, 0),Block.LADDER)
                    || MoveUtils.isNearBlock(distance.getFrom(),Block.LADDER) || MoveUtils.isNearBlock(player.getPosition(),Block.STILL_WATER)
                    || (!MoveUtils.isNearBlock(distance.getTo().clone().subtract(0, 1.5, 0),Block.STILL_WATER)
                    && !distance.getTo().clone().subtract(0, 0.5, 0).getLevelBlock().canPassThrough()))
                maxOffset += 0.15D;
            if (offset > maxOffset && movementCache.airTicks > 2) {
                return new CheckResult("ignored gravity (offset=" + offset + ", at=" + movementCache.airTicks + ")");
            }
        }

        return CheckResult.PASSED;
    }
}
