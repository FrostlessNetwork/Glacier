package network.frostless.glacier;

import com.google.common.base.Preconditions;
import com.grinderwolf.swm.api.SlimePlugin;
import com.grinderwolf.swm.api.world.SlimeWorld;
import lombok.Getter;
import lombok.Setter;
import network.frostless.bukkitapi.FrostbiteAPI;
import network.frostless.glacier.app.GlacierCoreGameLoader;
import network.frostless.glacier.slime.WorldManager;
import network.frostless.glacier.user.UserManagerImpl;
import network.frostless.glacierapi.slime.SlimeAPI;
import network.frostless.glacierapi.user.GameUser;
import network.frostless.glacierapi.user.UserManager;
import network.frostless.glacierapi.user.loader.UserDataLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.Bukkit;

import java.util.concurrent.ExecutionException;

/**
 * The entry point to the minigames API.
 *
 * @author RiceCX
 */
@Getter
@Setter
public class Glacier<T extends GameUser> {

    private static Glacier<?> instance;
    private static final Logger logger = LogManager.getLogger("Glacier");

    /* Glacier Dependencies */
    private FrostbiteAPI frostbite;

    private SlimePlugin slimePlugin;

    /* Internal Loading */
    @Setter
    @Getter
    private static GlacierCoreGameLoader<?> plugin;

    private UserManager userManager;
    private UserDataLoader<?> userDataLoader;

    /* Games API */
    private SlimeAPI worldManager;


    private Glacier() {
        instance = this;
        logger.info("Glacier API loading...");
        final long start = System.currentTimeMillis();
        if (plugin == null)
            throw new RuntimeException("Glacier is not loaded! Please do Glacier.setPlugin(plugin) before loading!");
        initializeDependencies();

        setUserDataLoader(plugin);
        setUserManager(new UserManagerImpl<>(frostbite));

        userManager.registerConnection();

        // Games API setup
        worldManager = new WorldManager(slimePlugin);

        // Load secondary
        loadSecondary();

        logger.info("Glacier API loaded in " + (System.currentTimeMillis() - start) + "ms");
    }

    private void loadSecondary() {
        worldManager.loadMap("ptbl-slime").whenComplete((map, err) -> {
            try {
                SlimeWorld slimeWorld = worldManager.generateMap(map).get();
                logger.info("Lobby map '{}' loaded!", slimeWorld.getName());

            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });
    }

    private void initializeDependencies() {
        /* Frostbite */
        frostbite = Bukkit.getServicesManager().load(FrostbiteAPI.class);
        Preconditions.checkNotNull(frostbite, "Frostbite (core) is not installed! Please install it to use Glacier!");

        /* AdvancedSlimeWorldManager */
        slimePlugin = (SlimePlugin) Bukkit.getPluginManager().getPlugin("SlimeWorldManager");
        Preconditions.checkNotNull(slimePlugin, "SlimeWorldManager is not installed! Please install it to use Glacier!");
    }

    public static Glacier<?> get() {
        if (instance == null) instance = new Glacier<>();

        return instance;
    }

    @SuppressWarnings("unchecked")
    public static <V extends GameUser> Glacier<V> get(Class<V> clazz) {
        return (Glacier<V>) get();
    }
}
