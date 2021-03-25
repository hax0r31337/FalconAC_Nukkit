package me.liuli.falcon.check.checks.killaura;

import cn.nukkit.Player;
import cn.nukkit.network.protocol.DataPacket;
import me.liuli.falcon.check.CheckBase;
import me.liuli.falcon.check.CheckType;

public class KAHit1 extends CheckBase {
    public KAHit1() {
        super(CheckType.KillAura,"hit","HIT1");
    }

    @Override
    public void onReceivePacket(Player player,DataPacket packet){

    }
}
