package network.frostless.glacier.items;

import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class ImmutableItem {

    private final ItemStack item;
    private final String signature;

    public ImmutableItem(Material material) {
        this(new ItemStack(material));
    }

    public ImmutableItem(ItemStack is) {
        this.signature = UUID.randomUUID().toString();

        var nbti = new NBTItem(is);
        nbti.setString(signature, "monkey");

        this.item = nbti.getItem();
    }

    public String getSignature() {
        return signature;
    }

    public static boolean compare(ItemStack i1, ImmutableItem i2) {
        NBTItem nbtItem = new NBTItem(i1);
        String string = nbtItem.getString(i2.getSignature());

        return string != null && string.equals("monkey");
    }

    public static boolean compare(ImmutableItem i1, ImmutableItem i2) {
        return i1.getSignature().equals(i2.getSignature());
    }



    public ItemStack itemStack() {
        return item;
    }
}
