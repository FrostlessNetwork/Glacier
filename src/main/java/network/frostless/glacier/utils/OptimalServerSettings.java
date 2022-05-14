package network.frostless.glacier.utils;

import network.frostless.glacier.Glacier;
import org.bukkit.Bukkit;

public class OptimalServerSettings {

    private static boolean silent = false;

    public static void check(boolean silent) {
        // Check if nether is enabled
        OptimalServerSettings.silent = silent;
        if (Bukkit.getWorlds().stream().anyMatch(world -> world.getEnvironment() == org.bukkit.World.Environment.NETHER))
            scream("Nether is enabled! Disabling this will decrease startup time!");

        // Check if end is enabled
        if (Bukkit.getWorlds().stream().anyMatch(world -> world.getEnvironment() == org.bukkit.World.Environment.THE_END))
            scream("End is enabled! Disabling this will decrease startup time!");

    }

    private static void scream(String message) {
        if (!silent) {
            Glacier.getLogger().warn(message);
        }
    }
}
