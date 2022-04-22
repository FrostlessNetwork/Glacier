package network.frostless.glacier.map;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
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
import java.util.concurrent.TimeUnit;

public abstract class MapManager<T extends MapMeta> {

    protected final Gson gson = new GsonBuilder()
            .registerTypeAdapter(Location.class, new LocationAdapter())
            .create();

    private final Cache<String, MapMeta> mapsCache;

    private final String gameType;

    public MapManager(String gameType) {
        mapsCache = CacheBuilder.newBuilder().expireAfterWrite(10, TimeUnit.MINUTES).removalListener(new MapUpdater(this)).maximumSize(1000).build();
        this.gameType = gameType;
        OffloadTask.offloadAsync(this::loadAndCacheMapsAsync);

    }

    public void loadAndCacheMapsAsync() {
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

        mapsCache.putAll(maps);
        Glacier.getLogger().info("Loaded " + maps.size() + " maps for " + gameType + " in " + (System.currentTimeMillis() - start) + "ms");
    }


    public MapMeta getMap(String name) {
        return mapsCache.getIfPresent(name);
    }

    protected abstract T deserializeMapMeta(JsonElement parentObject);

    protected abstract Class<T> getClazz();
}
