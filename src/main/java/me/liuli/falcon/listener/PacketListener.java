package me.liuli.falcon.listener;

import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.server.DataPacketReceiveEvent;
import cn.nukkit.event.server.DataPacketSendEvent;
import me.liuli.falcon.manage.CheckManager;

import java.util.concurrent.ThreadPoolExecutor;

public class PacketListener implements Listener {
    private final CheckManager checkManager;
    private final ThreadPoolExecutor threadPoolExecutor;

    public PacketListener(CheckManager checkManager,ThreadPoolExecutor threadPoolExecutor){
        this.threadPoolExecutor=threadPoolExecutor;
        this.checkManager=checkManager;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPacketReceive(DataPacketReceiveEvent event) {
        threadPoolExecutor.execute(() -> checkManager.handleReceivePacket(event.getPlayer(),event.getPacket()));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPacketSend(DataPacketSendEvent event) {
        threadPoolExecutor.execute(() -> checkManager.handleSendPacket(event.getPlayer(),event.getPacket()));
    }
}
