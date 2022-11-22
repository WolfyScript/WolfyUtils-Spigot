/*
 *       WolfyUtilities, APIs and Utilities for Minecraft Spigot plugins
 *                      Copyright (C) 2021  WolfyScript
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package me.wolfyscript.utilities.compatibility.plugins.mmoitems;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.wolfyscript.utilities.bukkit.compatibility.plugins.mmoitems.MMOItemsRef;
import io.lumine.mythic.lib.api.item.NBTItem;
import java.io.IOException;
import java.util.Objects;
import me.wolfyscript.utilities.api.inventory.custom_items.references.APIReference;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.api.item.mmoitem.MMOItem;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

/**
 * Links to MMOItems and saves the specified {@link Type} and Name of the item.
 */
public class MMOItemsRefImpl extends APIReference implements MMOItemsRef {

    private final Type itemType;
    private final String itemName;

    public MMOItemsRefImpl(Type itemType, String itemName) {
        super();
        this.itemType = itemType;
        this.itemName = itemName;
    }

    public MMOItemsRefImpl(MMOItemsRefImpl mmoItemsRefImpl) {
        super(mmoItemsRefImpl);
        this.itemName = mmoItemsRefImpl.itemName;
        this.itemType = mmoItemsRefImpl.itemType;
    }

    @Override
    public ItemStack getLinkedItem() {
        MMOItem item = MMOItems.plugin.getMMOItem(itemType, itemName);
        return item != null ? item.newBuilder().buildSilently() : null;
    }

    @Override
    public ItemStack getIdItem() {
        return getLinkedItem();
    }

    @Override
    public boolean isValidItem(ItemStack itemStack) {
        var nbtItem = NBTItem.get(itemStack);
        if (nbtItem.hasType()) {
            return Objects.equals(this.itemType, MMOItems.plugin.getTypes().get(nbtItem.getType())) && Objects.equals(this.itemName, nbtItem.getString("MMOITEMS_ITEM_ID"));
        }
        return false;
    }

    @Override
    public void serialize(JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeObjectFieldStart("mmoitems");
        gen.writeStringField("type", itemType.getId());
        gen.writeStringField("name", itemName);
        gen.writeEndObject();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MMOItemsRefImpl that)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(itemType, that.itemType) && Objects.equals(itemName, that.itemName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), itemType, itemName);
    }

    @Override
    public MMOItemsRefImpl clone() {
        return new MMOItemsRefImpl(this);
    }

    public static class Parser extends PluginParser<MMOItemsRefImpl> {

        public Parser() {
            super("MMOItems", "mmoitems");
        }

        @Override
        public @Nullable
        MMOItemsRefImpl construct(ItemStack itemStack) {
            NBTItem nbtItem = NBTItem.get(itemStack);
            if (nbtItem.hasType()) {
                Type type = MMOItems.plugin.getTypes().get(nbtItem.getType());
                String itemId = nbtItem.getString("MMOITEMS_ITEM_ID");
                return new MMOItemsRefImpl(type, itemId);
            }
            return null;
        }

        @Override
        public @Nullable
        MMOItemsRefImpl parse(JsonNode element) {
            if (element.has("type") && element.has("name")) {
                String typeID = element.get("type").asText();
                if (MMOItems.plugin.getTypes().has(typeID)) {
                    return new MMOItemsRefImpl(MMOItems.plugin.getTypes().get(typeID), element.get("name").asText());
                }
            }
            return null;
        }
    }
}
