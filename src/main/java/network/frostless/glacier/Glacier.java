package network.frostless.glacier;

import com.google.common.base.Preconditions;
import com.grinderwolf.swm.api.SlimePlugin;
import com.grinderwolf.swm.api.world.SlimeWorld;
import io.papermc.paper.chat.ChatRenderer;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import network.frostless.bukkitapi.FrostbiteAPI;
import network.frostless.glacier.app.GlacierCoreGameLoader;
import network.frostless.glacier.chat.AbstractChat;
import network.frostless.glacier.chat.DefaultGlacierChat;
import network.frostless.glacier.game.GameManagerImpl;
import network.frostless.glacier.lobby.Lobby;
import network.frostless.glacier.scoreboard.Scoreboards;
import network.frostless.glacier.slime.WorldManager;
import network.frostless.glacier.user.UserManagerImpl;
import network.frostless.glacier.user.Users;
import network.frostless.glacier.utils.LazyLocation;
import network.frostless.glacierapi.game.manager.GameManager;
import network.frostless.glacierapi.slime.SlimeAPI;
import network.frostless.glacierapi.user.GameUser;
import network.frostless.glacierapi.user.UserManager;
import network.frostless.glacierapi.user.loader.UserDataLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

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

    @Getter
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
    private GameManager gameManager;

    private SlimeAPI worldManager;

    private Scoreboards scoreboardManager;

    private AbstractChat chatHandler;

    private Lobby lobby;


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
        gameManager = new GameManagerImpl();
        scoreboardManager = new Scoreboards();
        chatHandler = new DefaultGlacierChat();

        frostbite.getChatManager().setChatRenderer(chatHandler);
        frostbite.getChatManager().setAudienceFilter(chatHandler);

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
