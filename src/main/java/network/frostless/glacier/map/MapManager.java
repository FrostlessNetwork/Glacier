package network.frostless.glacier.map;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import network.frostless.glacier.Glacier;
import network.frostless.glacier.async.OffloadTask;
import network.frostless.glacier.utils.LocationAdapter;
import network.frostless.glacierapi.map.MapMeta;
import org.bukkit.Location;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public abstract class MapManager<T extends MapMeta> {

    protected final Gson gson = new GsonBuilder()
            .registerTypeAdapter(Location.class, new LocationAdapter())
            .create();

    private final Map<String, MapMeta> mapsCache = Maps.newConcurrentMap();
    private final ReentrantReadWriteLock mapsCacheLock = new ReentrantReadWriteLock();
    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    private final String gameType;

    public MapManager(String gameType) {
        this.gameType = gameType;
        executor.scheduleAtFixedRate(this::loadAndCacheMapsAsync, 0, 5, TimeUnit.MINUTES);
    }

    public synchronized void loadAndCacheMapsAsync() {
        long start = System.currentTimeMillis();
        Glacier.getLogger().info("Loading maps for " + gameType + "...");
        Map<String, T> maps = new HashMap<>();

        try (Connection conn = Glacier.get().getWorldManager().getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM map_meta WHERE game_type = ?")) {
                ps.setString(1, gameType);

                ResultSet resultSet = ps.executeQuery();
                while (resultSet.next()) {
                    System.out.println(resultSet.getString("data"));
                    T data = deserializeMapMeta(gson.fromJson(resultSet.getString("data"), JsonObject.class));

                    maps.put(data.getName(), data);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        mapsCacheLock.writeLock().lock();
        try {
            mapsCache.clear();
            mapsCache.putAll(maps);
        } finally {
            mapsCacheLock.writeLock().unlock();
        }

        Glacier.getLogger().info("Loaded " + maps.size() + " maps for " + gameType + " in " + (System.currentTimeMillis() - start) + "ms");
    }


    public synchronized MapMeta getMap(String name) {
        mapsCacheLock.readLock().lock();
        try {
            return mapsCache.get(name);
        } finally {
            mapsCacheLock.readLock().unlock();
        }
    }

    public void close() {
        executor.shutdownNow();
    }

    protected abstract T deserializeMapMeta(JsonElement parentObject);

}
