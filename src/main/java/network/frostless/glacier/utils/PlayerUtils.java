package network.frostless.glacier.utils;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

public class PlayerUtils {

    public static void reset(final Player player) {
        if (player.isDead()) player.spigot().respawn();
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        player.setExp(0);
        player.setLevel(0);
        player.setTotalExperience(0);
        player.setFoodLevel(20);
        player.setHealth(20);
        player.setSaturation(20);
        player.setFireTicks(0);
        player.setAllowFlight(false);
        player.setFlying(false);
        player.setGameMode(GameMode.SURVIVAL);
        player.setWalkSpeed(0.2F);

        for (PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }
    }
}
