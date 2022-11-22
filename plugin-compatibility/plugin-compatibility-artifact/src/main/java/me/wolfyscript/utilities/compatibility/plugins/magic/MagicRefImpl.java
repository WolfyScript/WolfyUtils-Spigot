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

package me.wolfyscript.utilities.compatibility.plugins.magic;

import com.elmakers.mine.bukkit.api.magic.MagicAPI;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.wolfyscript.utilities.bukkit.compatibility.plugins.magic.MagicRef;
import java.io.IOException;
import java.util.Objects;
import com.wolfyscript.utilities.bukkit.items.references.APIReference;
import me.wolfyscript.utilities.util.inventory.ItemUtils;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

/**
 * Links to an item from Magic.
 */
public class MagicRefImpl extends APIReference implements MagicRef {

    private final String itemKey;

    public MagicRefImpl(String itemKey) {
        super();
        this.itemKey = itemKey;
    }

    public MagicRefImpl(MagicRefImpl ref) {
        super(ref);
        this.itemKey = ref.itemKey;
    }

    /**
     * @return The item from Magic with the specified itemKey; or null if not available.
     */
    @Override
    public ItemStack getLinkedItem() {
        return Objects.requireNonNullElse(Parser.magicAPI.getController().createItem(itemKey), ItemUtils.AIR);
    }

    @Override
    public ItemStack getIdItem() {
        return getLinkedItem();
    }

    @Override
    public boolean isValidItem(ItemStack itemStack) {
        return Objects.equals(Parser.magicAPI.getController().getItemKey(itemStack), itemKey);
    }

    @Override
    public void serialize(JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStringField("magic", itemKey);
    }

    @Override
    public MagicRefImpl clone() {
        return new MagicRefImpl(this);
    }

    public static class Parser extends PluginParser<MagicRefImpl> {

        private static MagicAPI magicAPI = null;

        public Parser() {
            super("Magic", "magic");
        }

        @Override
        public void init(Plugin plugin) {
            if(plugin instanceof MagicAPI api) {
                magicAPI = api;
            }
        }

        @Override
        public @Nullable MagicRefImpl construct(ItemStack itemStack) {
            if(magicAPI.isBrush(itemStack) || magicAPI.isSpell(itemStack) || magicAPI.isUpgrade(itemStack) || magicAPI.isWand(itemStack)) {
                return new MagicRefImpl(magicAPI.getItemKey(itemStack));
            }
            return null;
        }

        @Override
        public @Nullable MagicRefImpl parse(JsonNode element) {
            return new MagicRefImpl(element.asText());
        }
    }
}
