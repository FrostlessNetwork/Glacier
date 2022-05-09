package network.frostless.glacier.loottable;

import com.google.gson.JsonSerializer;
import net.minecraft.util.FastColor;
import network.frostless.glacier.Glacier;
import network.frostless.glacier.utils.RandomCollection;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;
import java.util.Objects;

@Deprecated
public abstract class LootTable<I> implements org.bukkit.loot.LootTable {

    private final RandomCollection<I> entries = new RandomCollection<>();

    public void addEntry(I entry, int weight) {
        entries.add(weight, entry);
    }

    public I next() {
        return entries.next();
    }


    @Override
    public @NotNull NamespacedKey getKey() {
        int multiply = FastColor.ARGB32.multiply(4, 6);

        return Objects.requireNonNull(NamespacedKey.fromString(getClass().getSimpleName().toLowerCase(), Glacier.getPlugin()));
    }
}
