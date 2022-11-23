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

package me.wolfyscript.utilities.compatibility.plugins.oraxen;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.wolfyscript.utilities.bukkit.compatibility.plugins.oraxen.OraxenRef;
import io.th0rgal.oraxen.items.OraxenItems;
import java.io.IOException;
import java.util.Objects;
import com.wolfyscript.utilities.bukkit.world.items.references.APIReference;
import com.wolfyscript.utilities.bukkit.world.inventory.ItemUtils;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

/**
 * Links to Oraxen and saves the specified id of the item.
 */
public class OraxenRefImpl extends APIReference implements OraxenRef {

    private final String itemID;

    public OraxenRefImpl(String itemID) {
        super();
        this.itemID = itemID;
    }

    public OraxenRefImpl(OraxenRefImpl oraxenRefImpl) {
        super(oraxenRefImpl);
        this.itemID = oraxenRefImpl.itemID;
    }

    @Override
    public ItemStack getLinkedItem() {
        if (OraxenItems.exists(itemID)) {
            return OraxenItems.getItemById(itemID).build();
        }
        return ItemUtils.AIR;
    }

    @Override
    public ItemStack getIdItem() {
        return getLinkedItem();
    }

    @Override
    public boolean isValidItem(ItemStack itemStack) {
        String itemId = OraxenItems.getIdByItem(itemStack);
        if (itemId != null && !itemId.isEmpty()) {
            return Objects.equals(this.itemID, itemId);
        }
        return false;
    }

    @Override
    public void serialize(JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStringField("oraxen", itemID);
    }

    @Override
    public String getItemID() {
        return itemID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OraxenRefImpl oraxenRefImpl)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(itemID, oraxenRefImpl.itemID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), itemID);
    }

    @Override
    public OraxenRefImpl clone() {
        return new OraxenRefImpl(this);
    }

    public static class Parser extends PluginParser<OraxenRefImpl> {

        public Parser() {
            super("Oraxen", "oraxen");
        }

        @Override
        public @Nullable OraxenRefImpl construct(ItemStack itemStack) {
            String itemId = OraxenItems.getIdByItem(itemStack);
            if (itemId != null && !itemId.isEmpty()) {
                return new OraxenRefImpl(itemId);
            }
            return null;
        }

        @Override
        public @Nullable OraxenRefImpl parse(JsonNode element) {
            return new OraxenRefImpl(element.asText());
        }
    }
}
