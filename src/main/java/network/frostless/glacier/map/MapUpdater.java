package network.frostless.glacier.map;

import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import network.frostless.glacier.async.OffloadTask;
import network.frostless.glacierapi.map.MapMeta;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

public class MapUpdater<V extends MapMeta> implements RemovalListener<String, MapMeta> {

    private final Logger logger = LogManager.getLogger("Glacier Map Updater");
    private final MapManager<V> mapManager;

    public MapUpdater(MapManager<V> mapManager) {
        this.mapManager = mapManager;
    }

    @Override
    public void onRemoval(@NotNull RemovalNotification<String, MapMeta> notification) {
        OffloadTask.offloadAsync(mapManager::loadAndCacheMapsAsync);

        logger.info("Maps have been removed from the cache, time to load them again!");
    }
}
