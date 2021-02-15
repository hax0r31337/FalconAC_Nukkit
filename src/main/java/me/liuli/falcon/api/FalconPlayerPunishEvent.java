package me.liuli.falcon.api;

import cn.nukkit.Player;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.Event;
import cn.nukkit.event.HandlerList;
import me.liuli.falcon.manager.PunishResult;

public class FalconPlayerPunishEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    public Player player;
    public PunishResult result;

    public FalconPlayerPunishEvent(Player player, PunishResult result) {
        this.player = player;
        this.result = result;
    }

    public static HandlerList getHandlers() {
        return handlers;
    }
}
