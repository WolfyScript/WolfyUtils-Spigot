package me.wolfyscript.utilities.compatibility.plugins.fancybags;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializerProvider;
import de.tr7zw.changeme.nbtapi.NBTItem;
import de.tr7zw.changeme.nbtapi.NBTType;
import java.io.IOException;
import me.chickenstyle.backpack.Backpack;
import me.chickenstyle.backpack.Utils;
import me.chickenstyle.backpack.configs.CustomBackpacks;
import me.wolfyscript.utilities.api.inventory.custom_items.references.APIReference;
import me.wolfyscript.utilities.compatibility.plugins.FancyBagsImpl;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public class FancyBagsItemsRef extends APIReference {

    private static final String ID_TAG = "BackpackID";
    private final int id;

    public FancyBagsItemsRef(int id) {
        this.id = id;
    }

    private FancyBagsItemsRef(FancyBagsItemsRef ref) {
        this.id = ref.id;
    }

    @Override
    public ItemStack getLinkedItem() {
        Backpack bag = CustomBackpacks.getBackpack(id);
        if (bag != null) {
            return Utils.createBackpackItemStack(bag.getName(), bag.getTexture(), bag.getSlotsAmount(), bag.getId());
        }
        return new ItemStack(Material.AIR);
    }

    @Override
    public boolean isValidItem(ItemStack itemStack) {
        NBTItem nbtItem = new NBTItem(itemStack);
        if (nbtItem.hasKey(ID_TAG) && nbtItem.getType(ID_TAG).equals(NBTType.NBTTagInt)) {
            return nbtItem.getInteger(ID_TAG) == id;
        }
        return false;
    }

    @Override
    public void serialize(JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeNumberField("fancybags", id);
    }

    @Override
    public APIReference clone() {
        return new FancyBagsItemsRef(this);
    }

    public static class Parser extends PluginParser<FancyBagsItemsRef> {

        public Parser() {
            super(FancyBagsImpl.KEY, "fancybags");
        }

        @Override
        public @Nullable FancyBagsItemsRef construct(ItemStack itemStack) {
            NBTItem nbtItem = new NBTItem(itemStack);
            if (nbtItem.hasKey(ID_TAG) && nbtItem.getType(ID_TAG).equals(NBTType.NBTTagInt)) {
                return new FancyBagsItemsRef(nbtItem.getInteger(ID_TAG));
            }
            return null;
        }

        @Override
        public @Nullable FancyBagsItemsRef parse(JsonNode element) {
            return new FancyBagsItemsRef(element.asInt());
        }
    }
}
