package network.frostless.glacierapi.lootmanager;

import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.Map;

public interface LootManager {

    void loadLootTables();

    Gson getGson();

    String getKey();


    void injectCraftLootTables(Map<ResourceLocation, JsonElement> lootTableMap);
    ImmutableList<LootTable> getCraftLootTables();

    Map<ResourceLocation, LootTable> getNonCraftLootTables();

    ImmutableList<org.bukkit.loot.LootTable> getLootTables();

    void setLootTables(Map<ResourceLocation, LootTable> lootTables);
}
