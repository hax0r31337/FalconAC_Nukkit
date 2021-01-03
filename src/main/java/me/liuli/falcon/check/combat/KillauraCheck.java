package me.liuli.falcon.check.combat;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.level.Location;
import cn.nukkit.math.Vector3f;
import cn.nukkit.network.protocol.AnimatePacket;
import me.liuli.falcon.cache.CheckCache;
import me.liuli.falcon.manager.AnticheatManager;
import me.liuli.falcon.manager.CheckResult;
import me.liuli.falcon.manager.CheckType;
import me.liuli.falcon.utils.LocationUtils;

import java.util.*;

public class KillauraCheck {
    private static Map<UUID,Long> swingDataMap=new HashMap<>();
    public static void processSwing(Player player, AnimatePacket packet){
        swingDataMap.put(player.getUniqueId(),new Date().getTime());
    }
    public static void addSwingCheckTimer(Player player){
        Timer timer = new Timer("setTimeout", true);
        timer.schedule(new TimerTask(){
            @Override
            public void run() {
                CheckResult checkResult=KillauraCheck.checkSwing(player);
                if(checkResult.failed()){
                    AnticheatManager.addVL(CheckCache.get(player), CheckType.KA_NOSWING);
                }
            }
        }, CheckType.KA_NOSWING.otherData.getInteger("swing")/2);
    }
    private static CheckResult checkSwing(Player player) {
        if(swingDataMap.get(player.getUniqueId())==null)
            return CheckResult.FAILED;
        long lastSwingTime=swingDataMap.remove(player.getUniqueId());
        if((new Date().getTime()-lastSwingTime)>CheckType.KA_NOSWING.otherData.getInteger("swing")){
            return CheckResult.FAILED;
        }
        return CheckResult.PASSED;
    }
    public static CheckResult checkAngle(Player player, EntityDamageByEntityEvent event) {
        Entity entity = event.getEntity();

        if (entity.isAlive()) {
            double yawDifference = calculateYawDifference(player.getLocation(), entity.getLocation());
            double angleDifference = Math.abs(180 - Math.abs(Math.abs(yawDifference - player.yaw) - 180));
            if (Math.round(angleDifference) > CheckType.KILLAURA.otherData.getInteger("angle")) {
                return CheckResult.FAILED;
            }
        }
        return CheckResult.PASSED;
    }

    public static CheckResult checkReach(Player player, Entity target) {
        float allowedReach=player.gamemode!=1?CheckType.KILLAURA.otherData.getFloat("reach-common"):CheckType.KILLAURA.otherData.getFloat("reach-creative");
        if (player.gamemode == 1)
            allowedReach += 1.5D;
        allowedReach += player.getPing() * CheckType.KILLAURA.otherData.getFloat("reach-ping");
        if (target instanceof Player) {
            allowedReach += ((Player) target).getPing() * CheckType.KILLAURA.otherData.getFloat("reach-ping");
        }
        // Velocity compensation
        double reachedDistance = target.getLocation().distance(player.getLocation());
        if (reachedDistance > allowedReach)
            return CheckResult.FAILED;
        return CheckResult.PASSED;
    }

    public static double calculateYawDifference(Location from, Location to) {
        Location clonedFrom = from.clone();
        Vector3f vector=new Vector3f((float)(to.x-clonedFrom.x),(float)(to.y-clonedFrom.y),(float)(to.z-clonedFrom.z));
        return LocationUtils.setDirection(vector,clonedFrom).getYaw();
    }
}
