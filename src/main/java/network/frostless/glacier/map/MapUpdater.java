package network.frostless.glacier.map;

import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import network.frostless.glacier.async.OffloadTask;
import network.frostless.glacierapi.map.MapMeta;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MapUpdater implements RemovalListener<String, MapMeta> {

    private final Logger logger = LogManager.getLogger("Glacier Map Updater");
    private final MapManager mapManager;

    public MapUpdater(MapManager mapManager) {
        this.mapManager = mapManager;
    }

    @Override
    public void onRemoval(RemovalNotification<String, MapMeta> notification) {
        OffloadTask.offloadAsync(mapManager::loadAndCacheMapsAsync);

        logger.info("Maps have been removed from the cache, time to load them again!");
    }
}
