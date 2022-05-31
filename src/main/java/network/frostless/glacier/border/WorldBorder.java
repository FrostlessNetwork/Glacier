package network.frostless.glacier.border;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_18_R2.CraftWorld;
import org.bukkit.entity.Player;

public class WorldBorder {

    public static void create(Player player, Location center) {
        net.minecraft.world.level.border.WorldBorder worldBorder = new net.minecraft.world.level.border.WorldBorder();
        worldBorder.world = ((CraftWorld) player.getWorld()).getHandle();
        worldBorder.setCenter(center.getX(), center.getZ());


    }
}
