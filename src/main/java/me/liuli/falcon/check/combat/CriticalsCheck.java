package me.liuli.falcon.check.combat;

import cn.nukkit.Player;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.potion.Effect;
import me.liuli.falcon.manager.CheckResult;
import me.liuli.falcon.utils.LocationUtils;

public class CriticalsCheck {
    public static CheckResult doDamageEvent(EntityDamageByEntityEvent event) {
        Player player = (Player) event.getDamager();
        if (isCritical(player)) {
            if ((player.getLocation().getY() % 1.0 == 0 || player.getLocation().getY() % 0.5 == 0)
                    && player.getLocation().clone().subtract(0, 1.0, 0).getLevelBlock().isSolid()) {
                return new CheckResult("tried to do a critical without needed conditions");
            }
        }
        return CheckResult.PASSED;
    }
    private static boolean isCritical(Player player) {
        return !player.isOnGround() && !player.hasEffect(Effect.BLINDNESS)
                && !LocationUtils.isHoveringOverWater(player.getLocation(),25)
                && !player.getLocation().getLevelBlock().canBeClimbed();
    }
}
