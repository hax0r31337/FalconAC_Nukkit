package me.liuli.falcon.check.combat;

import cn.nukkit.Player;
import cn.nukkit.event.player.PlayerMoveEvent;
import me.liuli.falcon.manager.CheckResult;
import me.liuli.falcon.manager.CheckType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AimbotCheck {
    private static Map<UUID,Integer> lastRot=new HashMap<>();
    private static Map<UUID,Integer> sameRot=new HashMap<>();
    public static CheckResult check(Player player, PlayerMoveEvent event){
        int maxRot=CheckType.AIMBOT.otherData.getInteger("maxRotate");
        int smoothMin=CheckType.AIMBOT.otherData.getInteger("smoothMin");
        int smoothSame=CheckType.AIMBOT.otherData.getInteger("smoothSame");

        int dYaw = (int) Math.abs(event.getTo().getYaw() - event.getFrom().getYaw());
        int dPitch = (int) Math.abs(event.getTo().getPitch() - event.getFrom().getPitch());
        int moveLength = (int) Math.sqrt(Math.pow(dYaw, 2.0D)+Math.pow(dPitch, 2.0D));
        if(moveLength>maxRot&&moveLength<(360-maxRot)){
            return new CheckResult("Rotate too fast(speed="+moveLength+",max="+maxRot+")");
        }

        UUID playerUUID=player.getUniqueId();
        if(!lastRot.containsKey(playerUUID)) lastRot.put(playerUUID,0);
        if(!sameRot.containsKey(playerUUID)) sameRot.put(playerUUID,0);
        if(moveLength>smoothMin&&(lastRot.get(playerUUID)/smoothMin)==(moveLength/smoothMin)){
            sameRot.put(playerUUID,sameRot.get(playerUUID)+1);
        }else{
            sameRot.put(playerUUID,0);
        }
        lastRot.put(playerUUID,moveLength);
        if(sameRot.get(playerUUID)>smoothSame){
            return new CheckResult("Rotate too smooth(speed="+moveLength+",times="+sameRot.get(playerUUID)+")");
        }
        return CheckResult.PASSED;
    }
}
