package network.frostless.glacier;

import com.google.common.base.Preconditions;
import com.grinderwolf.swm.api.SlimePlugin;
import lombok.Getter;
import lombok.Setter;
import network.frostless.bukkitapi.FrostbiteAPI;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.Bukkit;

/**
 * The entry point to the minigames API.
 * @author RiceCX
 */
@Getter
@Setter
public class Glacier {

    private static Glacier instance;
    private static final Logger logger = LogManager.getLogger("Glacier");

    /* Glacier Dependencies */
    private static FrostbiteAPI frostbite;

    private static SlimePlugin slimePlugin;

    /* Games API */


    private Glacier() {
        logger.info("Glacier API loading...");
        final long start = System.currentTimeMillis();
        initializeDependencies();

        logger.info("Glacier API loaded in " + (System.currentTimeMillis() - start) + "ms");
    }


    private void initializeDependencies() {
        /* Frostbite */
        frostbite = Bukkit.getServicesManager().load(FrostbiteAPI.class);
        Preconditions.checkNotNull(frostbite, "Frostbite (core) is not installed! Please install it to use Glacier!");

        /* AdvancedSlimeWorldManager */
        slimePlugin = (SlimePlugin) Bukkit.getPluginManager().getPlugin("SlimeWorldManager");
        Preconditions.checkNotNull(slimePlugin, "SlimeWorldManager is not installed! Please install it to use Glacier!");


    }

    public static Glacier get() {
        if (instance == null) {
            instance = new Glacier();
        }
        return instance;
    }
}
