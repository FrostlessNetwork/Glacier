package network.frostless.glacier;

import com.google.common.base.Preconditions;
import com.grinderwolf.swm.api.SlimePlugin;
import com.grinderwolf.swm.api.world.SlimeWorld;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.minimessage.MiniMessage;
import network.frostless.bukkitapi.FrostbiteAPI;
import network.frostless.glacier.app.GlacierCoreGameLoader;
import network.frostless.glacier.chat.AbstractChat;
import network.frostless.glacier.chat.DefaultGlacierChat;
import network.frostless.glacier.commands.GlacierCommands;
import network.frostless.glacier.commands.core.GameAutoCompleter;
import network.frostless.glacier.config.GlacierConfig;
import network.frostless.glacier.countdown.CountdownManager;
import network.frostless.glacier.exceptions.GameNotFoundException;
import network.frostless.glacier.exceptions.GlacierExceptionAdapter;
import network.frostless.glacier.game.GameBoardManager;
import network.frostless.glacier.game.GameManagerImpl;
import network.frostless.glacier.map.MapManager;
import network.frostless.glacier.team.Team;
import network.frostless.glacier.utils.OptimalServerSettings;
import network.frostless.glacierapi.game.Game;
import network.frostless.glacierapi.game.GameType;
import network.frostless.glacierapi.lobby.Lobby;
import network.frostless.glacier.rank.RankManager;
import network.frostless.glacier.scoreboard.Scoreboards;
import network.frostless.glacier.slime.WorldManager;
import network.frostless.glacier.user.UserManagerImpl;

import network.frostless.glacier.vote.VoteManager;
import network.frostless.glacierapi.game.manager.GameManager;
import network.frostless.glacierapi.map.MapMeta;
import network.frostless.glacierapi.slime.SlimeAPI;
import network.frostless.glacierapi.user.GameUser;
import network.frostless.glacierapi.user.UserManager;
import network.frostless.glacierapi.user.loader.UserDataLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.CommandException;
import org.bukkit.event.Event;
import org.bukkit.scheduler.BukkitScheduler;
import org.spongepowered.configurate.ConfigurateException;
import revxrsal.commands.bukkit.BukkitCommandHandler;

import java.nio.file.Path;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * The entry point to the minigames API.
 *
 * @author RiceCX
 */
@Getter
@Setter
public class Glacier<T extends GameUser, U extends Team<T>> {

    public static MiniMessage miniMessage = MiniMessage.miniMessage();

    private static Glacier<?, ?> instance;

    @Getter
    private static final Logger logger = LogManager.getLogger("Glacier");

    @Getter
    private static GlacierConfig config;

    private final Executor executorService = Executors.newCachedThreadPool(r -> new Thread(r, "Glacier-Thread"));

    /* Glacier Dependencies */
    private FrostbiteAPI frostbite;

    private SlimePlugin slimePlugin;

    /* Internal Loading */
    @Setter
    @Getter
    private static GlacierCoreGameLoader<?, ?> plugin;

    @Getter
    private BukkitCommandHandler commandHandler;

    @Setter
    @Getter
    private static GameType gameType;

    private UserManager userManager;
    private UserDataLoader<?> userDataLoader;

    /* Games API */
    private AbstractChat chatHandler;

    private GameManager gameManager;
    private SlimeAPI worldManager;
    private Scoreboards scoreboardManager;
    private MapManager<? extends MapMeta> mapManager;

    @Getter
    private VoteManager voteManager;
    private CountdownManager countdownManager;

    private GameBoardManager<T, U> gameBoard = new GameBoardManager<>();


    private Lobby lobby;


    private Glacier() {
        instance = this;
        logger.info("Glacier API loading...");
        final long start = System.currentTimeMillis();
        if (plugin == null)
            throw new RuntimeException("Glacier is not loaded! Please do Glacier.setPlugin(plugin) before loading!");
        initializeDependencies();
        loadConfig();
        OptimalServerSettings.check(false);

        setUserDataLoader(plugin);
        setUserManager(new UserManagerImpl<>(frostbite));

        getPlugin().registerPreConnections();
        userManager.registerConnection();

        // Games API setup
        executorService.execute(RankManager::fetchAllRanks);
        countdownManager = new CountdownManager();
        worldManager = new WorldManager(slimePlugin);
        gameManager = new GameManagerImpl();
        scoreboardManager = new Scoreboards();
        chatHandler = new DefaultGlacierChat();

        frostbite.getChatManager().setChatRenderer(chatHandler);
        frostbite.getChatManager().setAudienceFilter(chatHandler);

        // Load secondary
        loadSecondary();

        logger.info("Glacier API loaded in " + (System.currentTimeMillis() - start) + "ms");

        voteManager = new VoteManager();
    }

    private void loadCommands() {
        commandHandler.register(
                new GlacierCommands()
        );
    }

    private void loadConfig() {
        config = new GlacierConfig();
        config.setFilePath(Path.of(plugin.getDataFolder().getAbsolutePath() + "/glacier.yml"));
        try {
            config.load();
        } catch (ConfigurateException e) {
            e.printStackTrace();
            plugin.getServer().shutdown();
        }
    }

    private void loadSecondary() {
        loadCommands();
        String lobbyMap = getPlugin().getConfig().getString("lobby.map");
        if (lobbyMap == null) throw new RuntimeException("Lobby map is not set!");

        worldManager.loadMap(lobbyMap).whenComplete((map, err) -> {
            try {
                SlimeWorld slimeWorld = worldManager.generateMap(map).get();
                logger.info("Lobby map '{}' loaded!", slimeWorld.getName());

            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });

        countdownManager.startCountdowns();
    }

    private void initializeDependencies() {
        /* Frostbite */
        frostbite = Bukkit.getServicesManager().load(FrostbiteAPI.class);
        Preconditions.checkNotNull(frostbite, "Frostbite (core) is not installed! Please install it to use Glacier!");

        /* AdvancedSlimeWorldManager */
        slimePlugin = (SlimePlugin) Bukkit.getPluginManager().getPlugin("SlimeWorldManager");
        Preconditions.checkNotNull(slimePlugin, "SlimeWorldManager is not installed! Please install it to use Glacier!");

        commandHandler = BukkitCommandHandler.create(getPlugin());

        commandHandler.setExceptionHandler(new GlacierExceptionAdapter());

        commandHandler.registerValueResolver(Game.class, ctx -> {
            String gameName = ctx.pop();
            Game<?, ?> game = gameManager.getGame(gameName);
            if (game == null) {
                throw new GameNotFoundException(gameName);
            }
            return game;
        });

        commandHandler.getAutoCompleter()
                .registerParameterSuggestions(Game.class, new GameAutoCompleter());
    }

    @SuppressWarnings("unchecked")
    public static <U extends GameUser, T extends Team<U>, G extends Game<U, T>> Glacier<U, T> get(Class<G> clazz) {
        if (instance == null)
            new Glacier<>();
        return (Glacier<U, T>) instance;
    }

    public static Glacier<?, ?> get() {
        if (instance == null) instance = new Glacier<>();

        return instance;
    }

//    @SuppressWarnings("unchecked")
//    public static <U extends GameUser, V extends Team<U>> Glacier<U, V> get(Class<U> clazz) {
//        return (Glacier<U, V>) get();
//    }


    public void setChatHandler(AbstractChat chatHandler) {
        this.chatHandler = chatHandler;
        frostbite.getChatManager().setChatRenderer(chatHandler);
        frostbite.getChatManager().setAudienceFilter(chatHandler);
    }

    public static BukkitScheduler scheduler() {
        return getPlugin().getServer().getScheduler();
    }

    public static Server server() {
        return getPlugin().getServer();
    }

    public static void callEvent(Event event) {
        Bukkit.getPluginManager().callEvent(event);
    }
}
