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

package me.wolfyscript.utilities.compatibility.plugins.mythicmobs;

import com.fasterxml.jackson.databind.JsonNode;
import io.lumine.xikage.mythicmobs.MythicMobs;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

/**
 * Links to MythicMobs items and saves the specified item type.
 * <p>
 * For items to be detected by plugins in-game you need to add an additional Option to your MythicMobs item!
 * <pre>
 * Options:
 *     AppendType: true
 * </pre>
 */
public class MythicMobsRefImpl extends AbstractMythicMobsRef {

    public MythicMobsRefImpl(String itemName) {
        super(itemName);
    }

    public MythicMobsRefImpl(MythicMobsRefImpl mythicMobsRefImpl) {
        super(mythicMobsRefImpl);
    }

    @Override
    public ItemStack getLinkedItem() {
        return MythicMobs.inst().getItemManager().getItemStack(itemName);
    }

    @Override
    public MythicMobsRefImpl clone() {
        return new MythicMobsRefImpl(this);
    }

    public static class Parser extends AbstractMythicMobsRef.Parser<MythicMobsRefImpl> {

        @Override
        protected MythicMobsRefImpl construct(String value) {
            if (MythicMobs.inst().getItemManager().getItem(value).isPresent()) {
                return new MythicMobsRefImpl(value);
            }
            return null;
        }

        @Override
        public @Nullable
        MythicMobsRefImpl parse(JsonNode element) {
            return new MythicMobsRefImpl(element.asText());
        }
    }
}
