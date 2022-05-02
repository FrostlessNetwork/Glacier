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
import network.frostless.glacierapi.events.user.UserJoinEvent;
import network.frostless.glacierapi.user.GameUser;
import network.frostless.glacierapi.user.UserManager;
import network.frostless.glacierapi.user.loader.UserLoaderResult;
import network.frostless.serverapi.data.UserObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class UserManagerImpl<T extends GameUser> implements UserManager {

    private final Logger logger = LogManager.getLogger("Glacier User Manager");

    private boolean debug = false;
    private final FrostbiteAPI frostbite;
    private final Gson gson = new Gson();

    private final Cache<UUID, String> loadCache = CacheBuilder.newBuilder().maximumSize(100).expireAfterWrite(1, TimeUnit.MINUTES).build();
    private final Map<UUID, Map.Entry<? extends GameUser, String>> userCache = Maps.newConcurrentMap();


    private final ConnectionSource connectionSource;
    private Dao<T, ?> userDao;

    public UserManagerImpl(FrostbiteAPI frostbite) {
        this.frostbite = frostbite;
        this.connectionSource = frostbite.getUserManager().getConnectionSource();

        debug = Glacier.getPlugin().getConfig().getBoolean("debug", false);

        Glacier.getPlugin().registerListeners(new UserLoginListener());
    }

    @Override
    public CompletableFuture<UserLoaderResult> verifyUser(GlobalUser user) {
        CompletableFuture<UserLoaderResult> future = new CompletableFuture<>();

        if (debug) {
            logger.info("Verifying user {}", user.getUuid());
            if (user.getUuid().equals(UUID.fromString("cd19760f-8345-319b-80bb-acce521dc780")) || user.getUuid().equals(UUID.fromString("4fd22264-21dd-380d-ab71-fd66aea70f6e"))) {
                loadCache.put(user.getUuid(), Glacier.get().getGameManager().getRandomIdentifier());
                return CompletableFuture.completedFuture(UserLoaderResult.ALLOWED);
            }
        }

        RedisFuture<String> userObjectFuture = frostbite.getRedis().async().hget("users", user.getUuid().toString());

        userObjectFuture
                .thenApply((c) -> gson.fromJson(c, UserObject.class))
                .exceptionally(c -> null)
                .whenComplete((userObject, err) -> {
                    if (userObject == null || userObject.getGameIdentifier() == null) {
                        logger.warn("User {} has no game object. Denying...", user.getUuid());
                        future.complete(UserLoaderResult.DENIED);
                        return;
                    }

                    logger.debug("Debug user object: {}", userObject);

                    // check if games map has the user's identifier
                    if (!Glacier.get().getGameManager().hasGame(userObject.getGameIdentifier())) {
                        logger.warn("User {} tried to join with a invalid game identifier. Denying...", user.getUuid());
                        future.complete(UserLoaderResult.DENIED);
                        return;
                    }

                    loadCache.put(user.getUuid(), userObject.getGameIdentifier());
                    future.complete(UserLoaderResult.ALLOWED);
                });

        return future;
    }

    @Override
    public void loadUser(Player player, GlobalUser globalUser) {
        if (userCache.containsKey(player.getUniqueId())) return;

        String gameIdentifier = loadCache.getIfPresent(player.getUniqueId());


        if (gameIdentifier == null) {
            logger.warn("User {} was not supposed to join this server. Denying...", player.getName());
            player.kick(Component.text("You were disconnected from the server. Please rejoin").color(TextColor.color(255, 0, 0)));
            return;
        }

        GameUser user = Glacier.get().getUserDataLoader().createUser(player.getName(), player.getUniqueId());
        user.setGameIdentifier(gameIdentifier);
        user.setId(globalUser.getId());
        user.setGlobalUser(globalUser);

        try (Connection connection = connectionSource.getReadOnlyConnection("luckperms_group_permissions").getUnderlyingConnection()) {
            Map<String, Boolean> groupPermissions = Maps.newHashMap();

            // mmm good sql query to get permissions : ) - RiceCX
            try (PreparedStatement statement = connection.prepareStatement("SELECT v.* FROM luckperms_group_permissions AS v JOIN(SELECT regexp_replace(t.permission, 'group.', '') FROM (SELECT permission from luckperms_user_permissions WHERE uuid=? AND permission LIKE 'group.%') t FETCH FIRST ROW ONLY) AS tc ON v.name=tc.regexp_replace")) {
                statement.setString(1, user.getUuid().toString());

                ResultSet resultSet = statement.executeQuery();

                while (resultSet.next()) {
                    String rankName = resultSet.getString("name");
                    if (user.getRank() == null && !rankName.equalsIgnoreCase("default")) user.setRank(rankName);
                    groupPermissions.put(resultSet.getString("permission"), resultSet.getBoolean("value"));
                }
            }

            PermissionAttachment permissionAttachment = player.addAttachment(Glacier.getPlugin());
            groupPermissions.forEach(permissionAttachment::setPermission);

            logger.info("Loaded permissions for {}", user.getUuid());
        } catch (SQLException exception) {
            exception.printStackTrace();
        }

        loadCache.invalidate(player.getUniqueId());
        user.getGame().addPlayer(user);
        userCache.put(player.getUniqueId(), Maps.immutableEntry(user, gameIdentifier));
    }

    @Override
    @SuppressWarnings("unchecked")
    public void unloadUser(Player player) {
        logger.info("Handling disconnect for {}", player.getName());
        Map.Entry<? extends GameUser, String> user = userCache.get(player.getUniqueId());

        if (user == null) return;

        try {
            userDao.createOrUpdate((T) user.getKey());
        } catch (SQLException e) {
            logger.error("Failed to save user {}", player.getName());
            e.printStackTrace();
        }

        user.getKey().getGame().removePlayer(user.getKey());
        loadCache.invalidate(player.getUniqueId());
        userCache.remove(player.getUniqueId());
    }

    @Override
    @SuppressWarnings("unchecked")
    public <V> V getOrCreate(UUID uuid) {
        return (V) userCache.get(uuid).getKey();
    }

    @Override
    public List<GameUser> getUsers() {
        return userCache.values().stream().map(Map.Entry::getKey).collect(Collectors.toList());
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

    @Override
    public Connection getConnection() throws SQLException {
        return connectionSource.getReadOnlyConnection("luckperms_group_permissions").getUnderlyingConnection();
    }
}
