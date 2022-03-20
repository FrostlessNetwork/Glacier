package network.frostless.glacier.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public record LazyLocation(String world, Number x, Number y, Number z) {

    public Location location() {
        return new Location(
            Bukkit.getWorld(world),
            x.doubleValue(),
            y.doubleValue(),
            z.doubleValue()
        );
    }
}
