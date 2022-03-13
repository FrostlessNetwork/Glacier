package network.frostless.glacier.user;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import io.lettuce.core.RedisFuture;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import network.frostless.bukkitapi.FrostbiteAPI;
import network.frostless.frostentities.entity.GlobalUser;
import network.frostless.glacier.Glacier;
import network.frostless.glacierapi.user.GameUser;
import network.frostless.glacierapi.user.UserManager;
import network.frostless.glacierapi.user.loader.UserLoaderResult;
import network.frostless.serverapi.data.UserObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class UserManagerImpl<T extends GameUser> implements UserManager {

    private final Logger logger = LogManager.getLogger();

    private final FrostbiteAPI frostbite;
    private final Gson gson = new Gson();

    private final Cache<UUID, String> loadCache = CacheBuilder.newBuilder().maximumSize(100).expireAfterWrite(1, TimeUnit.MINUTES).build();
    private final Map<UUID, Map.Entry<T, String>> userCache = Maps.newConcurrentMap();


    private final ConnectionSource connectionSource;
    private Dao<T, ?> userDao;

    public UserManagerImpl(FrostbiteAPI frostbite) {
        this.frostbite = frostbite;
        this.connectionSource = frostbite.getUserManager().getConnectionSource();

        Glacier.getPlugin().registerListeners(new UserLoginListener());
    }


    @Override
    public CompletableFuture<UserLoaderResult> verifyUser(GlobalUser user) {
        CompletableFuture<UserLoaderResult> future = new CompletableFuture<>();

        RedisFuture<String> userObjectFuture = frostbite.getRedis().async().hget("users", user.getUuid().toString());

        userObjectFuture
                .thenApply((c) -> {
                    logger.info("User object received. Attempting to deserialize...");
                    return gson.fromJson(c, UserObject.class);
                })
                .exceptionally(c -> {
                    logger.info("User object not found. Denying...");
                    return null;
                })
                .whenComplete((userObject, err) -> {
                    if (userObject == null || userObject.getGameIdentifier() == null) {
                        logger.info("User {} has no game object. Denying...", user.getUuid());
                        future.complete(UserLoaderResult.DENIED);
                        return;
                    }

                    logger.debug("Debug user object: {}", userObject);

                    // check if games map has the user's identifier

                    loadCache.put(user.getUuid(), userObject.getGameIdentifier());
                    future.complete(UserLoaderResult.ALLOWED);
                });

        return future;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void loadUser(Player player) {
        if (userCache.containsKey(player.getUniqueId())) return;

        String gameIdentifier = loadCache.getIfPresent(player.getUniqueId());


        if (gameIdentifier == null) {
            logger.warn("User {} was not supposed to join this server. Denying...", player.getName());
            player.kick(Component.text("You were disconnected from the server. Please rejoin").color(TextColor.color(255, 0, 0)));
            return;
        }

        T user = (T) Glacier.get().getUserDataLoader().createUser(player.getName(), player.getUniqueId());
        user.setGameIdentifier(gameIdentifier);

        loadCache.invalidate(player.getUniqueId());
        userCache.put(player.getUniqueId(), Maps.immutableEntry(user, gameIdentifier));
    }

    @Override
    public void unloadUser(Player player) {
        logger.info("Handling disconnect for {}", player.getName());
        Map.Entry<T, String> user = userCache.get(player.getUniqueId());

        if(user == null) return;

        try {
            userDao.createOrUpdate(user.getKey());
        } catch (SQLException e) {
            logger.error("Failed to save user {}", player.getName());
            e.printStackTrace();
        }

        loadCache.invalidate(player.getUniqueId());
        userCache.remove(player.getUniqueId());
    }

    @Override
    public <V> V getOrCreate(UUID uuid) {
        return null;
    }

    /**
     * Registers the database tables for the user manager.
     */
    @SuppressWarnings("unchecked")
    public void registerConnection() {
        Class<T> userClass = (Class<T>) Glacier.get().getUserDataLoader().getUserClass();
        try {
            userDao = DaoManager.createDao(connectionSource, userClass);
            if (!userDao.isTableExists()) TableUtils.createTable(connectionSource, userClass);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
