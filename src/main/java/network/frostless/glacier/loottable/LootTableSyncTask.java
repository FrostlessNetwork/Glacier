package network.frostless.glacier.loottable;

import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTables;
import network.frostless.glacier.Glacier;
import network.frostless.glacierapi.lootmanager.LootManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.NamespacedKey;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

/**
 * Despite the name, it is async!
 */
public class LootTableSyncTask implements Runnable {

    private final LootManager lootManager;
    private final Logger logger = LogManager.getLogger("Loot Table Sync");

    private static Gson CRAFT_GSON = null;

    static {
        Field[] declaredFields = LootTables.class.getDeclaredFields();
        for (Field field : declaredFields) {
            if (field.getType() == Gson.class) {
                field.setAccessible(true);
                try {
                    CRAFT_GSON = (Gson) field.get(null);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
                break;
            }
        }
    }

    public LootTableSyncTask(LootManager lootManager) {
        this.lootManager = lootManager;
    }

    @Override
    public void run() {
        logger.info("Starting loot table sync...");
        Map<ResourceLocation, LootTable> keptLootTables = lootManager.getNonCraftLootTables();
        var cltBuilder = CraftLootTableBuilder.create();

        // Fetch the new loot tables from database
        try (var conn = Glacier.get().getWorldManager().getConnection()) {
            try (var ps = conn.prepareStatement("SELECT * FROM loottables WHERE gametype = ?")) {
                ps.setString(1, Glacier.getGameType().name());

                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    var key = NamespacedKey.fromString(rs.getString("name"));
                    if (key != null) {
                        cltBuilder.add(key, lootManager.getGson().fromJson(rs.getString("data"), JsonElement.class));
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        logger.info("Found " + cltBuilder.lootTable().size() + " loot tables.");
        cltBuilder.lootTable().forEach((k, v) -> keptLootTables.merge(k, CRAFT_GSON.fromJson(v, LootTable.class), (key, value) -> value));


        lootManager.setLootTables(keptLootTables);
    }
}
