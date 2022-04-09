package network.frostless.glacier.map;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import network.frostless.frostcore.utils.ObjectUtil;
import network.frostless.glacier.Glacier;
import network.frostless.glacierapi.map.MapMeta;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public abstract class MapManager<T extends MapMeta> {
    private final Cache<String, MapMeta> mapsCache;


    public MapManager() {
        mapsCache = CacheBuilder.newBuilder().expireAfterWrite(10, TimeUnit.MINUTES).maximumSize(1000).build();
    }

    public void loadAndCacheMapsAsync() {
        Map<String, T> maps = new HashMap<>();

        try (Connection conn = Glacier.get().getUserManager().getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM frostless_map_meta WHERE game_type = ?")) {
                ps.setString(1, "GLACIER");

                ResultSet resultSet = ps.executeQuery();
                while (resultSet.next()) {
                    T map = ObjectUtil.createObject(getClazz());
                    map.setName(resultSet.getString("name"));
                    map.setLastModified(resultSet.getTimestamp("last_modified").getTime());
                    map.setVersion(resultSet.getInt("version"));

                    maps.put(map.getName(), deserializeMapMeta(map, resultSet.getString("data")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        mapsCache.putAll(maps);
    }

    protected abstract T deserializeMapMeta(T mapMeta, String serializedData);

    protected abstract Class<T> getClazz();
}
