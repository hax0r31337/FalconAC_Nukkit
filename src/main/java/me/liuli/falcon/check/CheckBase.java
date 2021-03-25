package me.liuli.falcon.check;

import cn.nukkit.Player;
import cn.nukkit.network.protocol.DataPacket;
import lombok.Getter;
import me.liuli.falcon.FalconAC;

public class CheckBase {
    @Getter
    private final CheckType checkType;
    @Getter
    private final String subCheck;
    @Getter
    private final String checkId;

    public CheckBase(CheckType checkType,String subCheck,String checkId){
        this.checkType=checkType;
        this.subCheck=subCheck;
        this.checkId=checkId;
        FalconAC.getCheckManager().registerCheck(this);
    }

    public String getName(){
        return checkType.name()+"["+checkId+"]";
    }

    public void onUpdate() {

    }

    public void onReceivePacket(Player player, DataPacket packet){

    }

    public void onSendPacket(Player player,DataPacket packet){

    }
}
