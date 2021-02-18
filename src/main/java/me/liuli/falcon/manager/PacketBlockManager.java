package me.liuli.falcon.manager;

import cn.nukkit.Player;

import java.util.ArrayList;
import java.util.UUID;

public class PacketBlockManager {
    private static final ArrayList<UUID> blockList = new ArrayList<>();

    public static void addBlock(Player player) {
        blockList.add(player.getUniqueId());
    }

    public static void removeBlock(Player player) {
        blockList.remove(player.getUniqueId());
    }

    public static boolean checkBlock(Player player) {
        return blockList.contains(player.getUniqueId());
    }
}
