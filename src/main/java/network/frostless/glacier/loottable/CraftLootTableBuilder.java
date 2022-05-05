package network.frostless.glacier.loottable;

import com.google.gson.JsonElement;
import net.minecraft.resources.ResourceLocation;
import org.bukkit.NamespacedKey;

import java.util.HashMap;
import java.util.Map;

public class CraftLootTableBuilder {

    public static Builder create() {
        return new Builder();
    }


    static class Builder {
        private final Map<ResourceLocation, JsonElement> lootTable = new HashMap<>();

        public Builder add(NamespacedKey key, JsonElement data) {
            lootTable.put(new ResourceLocation(key.getNamespace(), key.getKey()), data);
            return this;
        }

        public Builder add(ResourceLocation key, JsonElement data) {
            lootTable.put(key, data);
            return this;
        }

        public Map<ResourceLocation, JsonElement> lootTable() {
            return lootTable;
        }
    }
}
