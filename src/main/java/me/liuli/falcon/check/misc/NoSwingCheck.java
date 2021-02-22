package me.liuli.falcon.check.misc;

import cn.nukkit.Player;
import me.liuli.falcon.cache.CheckCache;
import me.liuli.falcon.manager.AnticheatManager;
import me.liuli.falcon.manager.CheckResult;
import me.liuli.falcon.manager.CheckType;

import java.util.Timer;
import java.util.TimerTask;

public class NoSwingCheck {
    public static void addSwingRecord(Player player) {
        CheckCache cache = CheckCache.get(player);
        cache.lastSwing = System.currentTimeMillis();
    }

    public static void check(Player player) {
        Timer timer = new Timer("setTimeout", true);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                CheckResult checkResult = checkSwing(player);
                if (checkResult.failed()) {
                    AnticheatManager.addVL(player, CheckType.NOSWING, checkResult);
                }
            }
        }, CheckType.NOSWING.otherData.getInteger("swing") / 2);
    }

    private static CheckResult checkSwing(Player player) {
        CheckCache cache = CheckCache.get(player);
        if(cache==null){
            return CheckResult.PASSED;
        }

        if ((System.currentTimeMillis() - cache.lastSwing) > CheckType.NOSWING.otherData.getInteger("swing")) {
            return new CheckResult("Attack a entity without swing(last=" + (System.currentTimeMillis() - cache.lastSwing) + ")");
        }
        return CheckResult.PASSED;
    }
}
