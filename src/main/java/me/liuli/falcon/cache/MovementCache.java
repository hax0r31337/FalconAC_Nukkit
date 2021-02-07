/*
 * AntiCheatReloaded for Bukkit and Spigot.
 * Copyright (c) 2012-2015 AntiCheat Team
 * Copyright (c) 2016-2020 Rammelkast
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package me.liuli.falcon.cache;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.level.Location;
import cn.nukkit.potion.Effect;
import me.liuli.falcon.utils.MoveUtils;

public class MovementCache {

    // Ticks in air
    public int airTicks = 0;
    // Ticks on ground
    public int groundTicks = 0;
    // Ticks on ice
    public int iceTicks = 0;
    // Ticks on slime
    public int slimeTicks = 0;
    // Ticks in air before last grounded moment
    public int airTicksBeforeGrounded = 0;
    // Ticks influenced by ice
    public int iceInfluenceTicks = 0;
    // Ticks influenced by slime
    public int slimeInfluenceTicks = 0;
    // Y motion of the movement
    public double motionY;
    // Previous Y motion of the movement
    public double lastMotionY;
    // Horizontal distance of movement
    public double distanceXZ;
    // Horizontal distance on x-axis of movement
    public double distanceX;
    // Horizontal distance on z-axis of movement
    public double distanceZ;
    // Previous horizontal distance of movement
    public double lastDistanceXZ;
    // Previous horizontal distance on x-axis of movement
    public double lastDistanceX;
    // Previous horizontal distance on z-axis of movement
    public double lastDistanceZ;
    // If the player touched the ground again this tick
    public boolean touchedGroundThisTick = false;
    // Last recorded distance
    public Distance lastDistance = new Distance();
    // Movement acceleration
    public double acceleration;
    // Is the block above solid
    public boolean topSolid;
    // Is the block below solid
    public boolean bottomSolid;
    // If the current movement is up a slab or stair
    public boolean halfMovement;
    // If the player is on the ground (determined clientside!)
    public boolean onGround;
    // Ticks counter for last halfMovement
    public int halfMovementHistoryCounter = 0;
    // Time of last teleport
    public long lastTeleport;
    // Elytra effect ticks
    public int elytraEffectTicks;
    // Used by Velocity check, represents the currently expected Y motion
    public double velocityExpectedMotionY;
    // Amount of ticks a player is sneaking
    public int sneakingTicks;
    // Ticks counter after being near a liquid
    public int nearLiquidTicks;
    // If the player has a speed effect
    public boolean hasSpeedEffect = false;
    // If the player had speed effect previous tick
    public boolean hadSpeedEffect = false;
    // If the player has a speed increasing effect
    public boolean hasIncreasingEffect = false;
    // If the player had a speed increasing effect previous tick
    public boolean hadIncreasingEffect = false;
    // Time of last update
    public long lastUpdate;

    public void handle(Player player, Location from, Location to, Distance distance) {
        this.onGround = player.isOnGround();

        double x = distance.getXDifference();
        double z = distance.getZDifference();
        this.lastDistanceXZ = this.distanceXZ;
        this.lastDistanceX = this.distanceX;
        this.lastDistanceZ = this.distanceZ;
        this.distanceXZ = Math.sqrt(x * x + z * z);
        this.distanceX = x;
        this.distanceZ = z;

        this.touchedGroundThisTick = false;
        this.halfMovement = false;
        if (!this.onGround) {
            this.groundTicks = 0;
            this.airTicks++;
        } else {
            if (this.airTicks > 0)
                this.touchedGroundThisTick = true;
            this.airTicksBeforeGrounded = this.airTicks;
            this.airTicks = 0;
            this.groundTicks++;
        }

        if (MoveUtils.couldBeOnIce(to)) {
            this.iceTicks++;
            this.iceInfluenceTicks = 60;
        } else {
            this.iceTicks = 0;
            if (this.iceInfluenceTicks > 0)
                this.iceInfluenceTicks--;
        }

        if (MoveUtils.couldBeOnSlime(to)) {
            this.slimeTicks++;
            this.slimeInfluenceTicks = 40;
        } else {
            this.slimeTicks = 0;
            if (this.slimeInfluenceTicks > 0)
                this.slimeInfluenceTicks--;
        }

        if (player.isGliding()) {
            this.elytraEffectTicks = 30;
        } else {
            if (this.elytraEffectTicks > 0)
                this.elytraEffectTicks--;
        }

        if (player.isSneaking())
            this.sneakingTicks++;
        else
            this.sneakingTicks = 0;

        if (MoveUtils.isNearBlock(player, Block.STILL_WATER))
            this.nearLiquidTicks = 8;
        else {
            if (this.nearLiquidTicks > 0)
                this.nearLiquidTicks--;
            else
                this.nearLiquidTicks = 0;
        }

        this.hadSpeedEffect = this.hasSpeedEffect;
        this.hasSpeedEffect = player.hasEffect(Effect.SPEED);

        this.hadIncreasingEffect = this.hasIncreasingEffect;
        this.hasIncreasingEffect = !player.getEffects().isEmpty();

        double lastDistanceSq = Math.sqrt(this.lastDistance.getXDifference() * this.lastDistance.getXDifference()
                + this.lastDistance.getZDifference() * this.lastDistance.getZDifference());
        double currentDistanceSq = Math.sqrt(distance.getXDifference() * distance.getXDifference()
                + distance.getZDifference() * distance.getZDifference());
        this.acceleration = currentDistanceSq - lastDistanceSq;

        this.lastMotionY = this.motionY;
        this.motionY = to.getY() - from.getY();

        Location top = to.clone().add(0, 2, 0);
        this.topSolid = top.getLevelBlock().isSolid();
        Location bottom = to.clone().add(0, -1, 0);
        this.bottomSolid = bottom.getLevelBlock().isSolid();

        if (this.motionY > 0.42D && this.motionY <= 0.5625D) {
            this.halfMovement = true;
            this.halfMovementHistoryCounter = 30;
        } else {
            if (this.halfMovementHistoryCounter > 0)
                this.halfMovementHistoryCounter--;
        }

        this.lastUpdate = System.currentTimeMillis();
    }
}
