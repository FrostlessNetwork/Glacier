package network.frostless.glacier.loottable;

import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import lombok.Getter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTables;
import network.frostless.glacierapi.lootmanager.LootManager;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class DefaultLootManager implements LootManager {
    private static final Method APPLY_LOOT_TABLE;
    private static final Field FIELD_TABLES;

    static {
        Method APPLY_LOOT_TABLE1 = null;
        Method[] declaredMethods = LootTables.class.getDeclaredMethods();
        for (Method declaredMethod : declaredMethods) {
            if (declaredMethod.getParameterTypes().length == 3 && declaredMethod.getModifiers() == Modifier.PROTECTED) {
                declaredMethod.setAccessible(true);
                APPLY_LOOT_TABLE1 = declaredMethod;
                break;
            }
        }
        APPLY_LOOT_TABLE = APPLY_LOOT_TABLE1;

        Field FIELD_TABLES1 = null;
        Field[] declaredFields = LootTables.class.getDeclaredFields();
        for (Field declaredField : declaredFields) {
            if (declaredField.getType() == Map.class && declaredField.getModifiers() == Modifier.PRIVATE) {
                declaredField.setAccessible(true);
                FIELD_TABLES1 = declaredField;
                break;
            }
        }
        FIELD_TABLES = FIELD_TABLES1;
    }

    @Getter
    private final String key;

    @Getter
    private final Gson gson = new GsonBuilder()
            .enableComplexMapKeySerialization()
            .create();

    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor(r -> new Thread(r, "LootTableUpdater"));
    private final LootTableSyncTask syncTask;

    public DefaultLootManager(@NotNull String key) {
        this.key = key;
        this.syncTask = new LootTableSyncTask(this);
        loadLootTables();
    }

    public void loadLootTables() {
        syncTask.run();

        executorService.scheduleAtFixedRate(syncTask, 0, 15, TimeUnit.MINUTES);
    }


    @Override
    public void injectCraftLootTables(Map<net.minecraft.resources.ResourceLocation, JsonElement> lootTableMap) {
        LootTables craftLootTable = MinecraftServer.getServer().getLootTables();
        try {
            APPLY_LOOT_TABLE.invoke(craftLootTable, lootTableMap, MinecraftServer.getServer().getResourceManager(), MinecraftServer.getServer().getProfiler());
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public ImmutableList<net.minecraft.world.level.storage.loot.LootTable> getCraftLootTables() {
        List<net.minecraft.world.level.storage.loot.LootTable> lootTables = new ArrayList<>();
        LootTables craftLootTable = MinecraftServer.getServer().getLootTables();
        for (ResourceLocation id : craftLootTable.getIds()) {
            if (id.getNamespace().equalsIgnoreCase(key))
                lootTables.add(craftLootTable.get(id));
        }

        return ImmutableList.copyOf(lootTables);
    }

    @Override
    public Map<ResourceLocation, LootTable> getNonCraftLootTables() {
        Map<ResourceLocation, LootTable> lootTables = new HashMap<>();
        LootTables craftLootTable = MinecraftServer.getServer().getLootTables();
        for (ResourceLocation id : craftLootTable.getIds()) {
            if (!id.getNamespace().equalsIgnoreCase(key))
                lootTables.put(id, craftLootTable.get(id));
        }

        return lootTables;
    }

    @Override
    public ImmutableList<org.bukkit.loot.LootTable> getLootTables() {
        LootTables craftLootTable = MinecraftServer.getServer().getLootTables();
        List<org.bukkit.loot.LootTable> lootTables = new ArrayList<>();
        for (ResourceLocation id : craftLootTable.getIds()) {
            if (id.getNamespace().equalsIgnoreCase(key))
                lootTables.add(Bukkit.getLootTable(new NamespacedKey(id.getNamespace(), id.getPath())));
        }
        return ImmutableList.copyOf(lootTables);
    }


    @Override
    public void setLootTables(Map<ResourceLocation, LootTable> lootTables) {
        LootTables craftLootTable = MinecraftServer.getServer().getLootTables();
        try {
            FIELD_TABLES.set(craftLootTable, lootTables);

            // Invert lootTables
            Map<LootTable, ResourceLocation> lootTableToKey = new HashMap<>();

            for (Map.Entry<ResourceLocation, LootTable> entry : lootTables.entrySet()) {
                lootTableToKey.put(entry.getValue(), entry.getKey());
            }
            craftLootTable.lootTableToKey = lootTableToKey;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public void removeLootTable(List<ResourceLocation> loc) {
        LootTables craftLootTable = MinecraftServer.getServer().getLootTables();
        Map<LootTable, ResourceLocation> craftLootTableToKey = craftLootTable.lootTableToKey;

        var keptLootTables = craftLootTableToKey.entrySet().stream().filter(entry -> !loc.contains(entry.getValue())).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        craftLootTable.lootTableToKey = keptLootTables;

        var inverted = keptLootTables.entrySet().stream().collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
        try {
            FIELD_TABLES.set(craftLootTable, inverted);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

    }
}
