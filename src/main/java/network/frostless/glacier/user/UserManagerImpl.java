package network.frostless.glacier.user;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;
import com.google.common.collect.Tables;
import com.google.gson.Gson;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import io.lettuce.core.RedisFuture;
import lombok.SneakyThrows;
import network.frostless.bukkitapi.FrostbiteAPI;
import network.frostless.frostentities.entity.GlobalUser;
import network.frostless.glacier.Glacier;
import network.frostless.glacierapi.user.GameUser;
import network.frostless.glacierapi.user.UserManager;
import network.frostless.glacierapi.user.loader.UserLoaderResult;
import network.frostless.serverapi.data.UserObject;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class UserManagerImpl<T extends GameUser> implements UserManager {


    private final FrostbiteAPI frostbite;
    private final Gson gson = new Gson();

    private final Map<UUID, Map.Entry<T, String>> userCache = Maps.newConcurrentMap();

    private final ConnectionSource connectionSource;
    private Dao<T, ?> userDao;

    public UserManagerImpl(FrostbiteAPI frostbite) {
        this.frostbite = frostbite;
        this.connectionSource = frostbite.getUserManager().getConnectionSource();

        registerConnection();
    }


    public CompletableFuture<UserLoaderResult> loadUser(GlobalUser user) {
        CompletableFuture<UserLoaderResult> future = new CompletableFuture<>();

        RedisFuture<String> userObjectFuture = frostbite.getRedis().async().hget("users", user.getUuid().toString());

        userObjectFuture.thenApply((c) -> gson.fromJson(c, UserObject.class)).exceptionally(c -> null).whenComplete((userObject, err) -> {
            if(userObject == null) {
                future.complete(UserLoaderResult.DENIED);
                return;
            }

            // check if games map has the user's identifier
            T gameUser = getOrCreate(user.getUuid());
            gameUser.setGameIdentifier(userObject.getGameIdentifier());

            userCache.put(user.getUuid(), Map.entry(gameUser, userObject.getGameIdentifier()));

            future.complete(UserLoaderResult.ALLOWED);
        });

        return future;
    }

    @Override
    @SneakyThrows
    @SuppressWarnings("unchecked")
    public <V> V getOrCreate(UUID uuid) {
        // check cache first
        if(userCache.containsKey(uuid)) return (V) userCache.get(uuid).getKey();

        V user = (V) userDao.queryBuilder().where().eq("uuid", uuid.toString()).queryForFirst();
        if(user == null) {
            Player player = Bukkit.getPlayer(uuid);
            if(player == null) throw new RuntimeException("Player " + uuid + " not found");
            user = (V) Glacier.get().getUserDataLoader().createUser(player.getName(), uuid);
        }

        return user;
    }

    @SuppressWarnings("unchecked")
    private void registerConnection() {
        Class<T> userClass = (Class<T>) Glacier.get().getUserDataLoader().getUserClass();
        try {
            userDao = DaoManager.createDao(connectionSource, userClass);
            if(!userDao.isTableExists()) TableUtils.createTable(connectionSource, userClass);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
