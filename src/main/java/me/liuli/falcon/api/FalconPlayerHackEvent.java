package me.liuli.falcon.api;

import cn.nukkit.Player;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.Event;
import cn.nukkit.event.HandlerList;
import me.liuli.falcon.manager.CheckResult;
import me.liuli.falcon.manager.CheckType;

public class FalconPlayerHackEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    public Player player;
    public CheckType checkType;
    public CheckResult checkResult;
    public float vl;
    public boolean flag;

    public FalconPlayerHackEvent(Player player, CheckType checkType, CheckResult checkResult, boolean flag) {
        this.player = player;
        this.checkType = checkType;
        this.checkResult = checkResult;
        this.vl = checkType.addVl;
        this.flag = flag;
    }

    public static HandlerList getHandlers() {
        return handlers;
    }
}
