package me.liuli.falcon.check.combat;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import me.liuli.falcon.cache.CheckCache;
import me.liuli.falcon.cache.MovementCache;
import me.liuli.falcon.manager.CheckResult;
import me.liuli.falcon.manager.CheckType;
import me.liuli.falcon.utils.MoveUtil;

public class VelocityCheck {
    public static CheckResult runCheck(Player player){
        CheckCache cache = CheckCache.get(player);
        if (cache == null)
            return CheckResult.PASSED;

        MovementCache movementCache=cache.movementCache;
        if (MoveUtil.isNearBlock(player, Block.STILL_WATER) || player.getAllowFlight())
            return CheckResult.PASSED;

        if(movementCache.inVelocity()) {
            //y motion
            if(movementCache.topSolid) {
                if (movementCache.velocityY > 0) {
                    double yPercentage = (movementCache.motionY / movementCache.velocityY) * 100;
                    if (yPercentage < 0)
                        yPercentage = 0;
                    if (yPercentage < CheckType.VELOCITY.otherData.getInteger("minimumPercentage")) {
                        doFlag(player, movementCache);
                        return new CheckResult("ignored server vertical velocity (pct=" + yPercentage + ")");
                    }
                    // Reset expected Y motion
                    movementCache.velocityY = 0;
                }
            }else{
                movementCache.resetVelocity();
            }
            //xz motion
            if(MoveUtil.isNearSolid(player.getPosition()) || MoveUtil.isNearSolid(player.getPosition().add(0,1,0))) {
                if (movementCache.velocityX != 0 && movementCache.velocityZ != 0) {
                    double xzPercentage = (xzCalc(movementCache.motionX, movementCache.motionZ) / xzCalc(movementCache.motionX, movementCache.velocityZ)) * 100;
                    if (xzPercentage < 0)
                        xzPercentage = 0;

                    if (xzPercentage < CheckType.VELOCITY.otherData.getInteger("minimumPercentage")) {
                        doFlag(player, movementCache);
                        return new CheckResult("ignored server horizontal velocity (pct=" + xzPercentage + ")");
                    }
                    movementCache.velocityX = 0;
                    movementCache.velocityZ = 0;
                }
            }else{
                movementCache.resetVelocity();
            }
        }
        return CheckResult.PASSED;
    }

    private static void doFlag(Player player,MovementCache movementCache){
        movementCache.resetVelocity();
//        if(Configuration.smartFlag){
//            Position targetPos=player.getPosition().clone();
//            targetPos.add(movementCache.velocityX,movementCache.velocityY,movementCache.velocityZ);
//            player.teleport(targetPos);
//        }
    }

    private static double xzCalc(double x,double z){
        return Math.sqrt(x * x + z * z);
    }
}
