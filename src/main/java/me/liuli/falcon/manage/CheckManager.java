package me.liuli.falcon.manage;

import cn.nukkit.Player;
import cn.nukkit.network.protocol.DataPacket;
import me.liuli.falcon.FalconAC;
import me.liuli.falcon.check.CheckBase;
import me.liuli.falcon.check.checks.killaura.KAHit1;

import java.util.ArrayList;

public class CheckManager {
    private final ArrayList<CheckBase> checks = new ArrayList<>();

    public void registerAll(){
        new KAHit1();
    }

    public void registerCheck(CheckBase check){
        checks.add(check);
        FalconAC.getInstance().getLogger().warning("Check "+check.getName()+" registered");
    }

    public void handleUpdate(){
        checks.forEach(CheckBase::onUpdate);
    }

    public void handleReceivePacket(Player player, DataPacket packet){
        checks.forEach((check) -> {
            check.onReceivePacket(player,packet);
        });
    }

    public void handleSendPacket(Player player, DataPacket packet){
        checks.forEach((check) -> {
            check.onSendPacket(player,packet);
        });
    }
}
