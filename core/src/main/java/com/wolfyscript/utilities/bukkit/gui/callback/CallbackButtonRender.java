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

package com.wolfyscript.utilities.bukkit.gui.callback;

import com.wolfyscript.utilities.bukkit.gui.GUIHolder;
import com.wolfyscript.utilities.bukkit.gui.button.Button;
import com.wolfyscript.utilities.bukkit.gui.cache.CustomCache;
import java.util.Optional;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

/**
 * @param  The type of the {@link CustomCache}
 */
public interface CallbackButtonRender<C extends CustomCache> {

    /**
     * Run when the button is rendered into the GUI.
     * The returned ItemStack will be set into the slot of the button.
     * Using the values HashMap you can replace specific Strings in the item names (e.g. replace placeholder from language file) with custom values.
     *
     * @param cache        The current cache of the GuiHandler
     * @param guiHandler   The current GuiHandler.
     * @param player       The current Player.
     * @param inventory The GUIInventory in which this render was called from.
     * @param itemStack    The current itemsStack of the button.
     * @param slot         The slot in which the button is rendered.
     * @return The itemStack that should be set into the GUI.
     */
    Result run(GUIHolder holder, C cache, Button button, int slot, ItemStack itemStack);

    /**
     * Contains the data that is used to render the button.
     *
     * @see CallbackButtonRender
     */
    class Result {

        private final ItemStack itemStack;
        private final TagResolver resolver;

        private Result(@Nullable ItemStack itemStack, @Nullable TagResolver resolver) {
            this.itemStack = itemStack;
            this.resolver = resolver;
        }

        private Result(@Nullable ItemStack itemStack, TagResolver... resolvers) {
            this(itemStack, resolvers == null || resolvers.length == 0 ? null : TagResolver.resolver(resolvers));
        }

        private Result(@Nullable ItemStack itemStack) {
            this(itemStack, (TagResolver) null);
        }

        /**
         * Creates a new UpdateResult of the specified ItemStack and TagResolvers.
         *
         * @param itemStack The ItemStack to render.
         * @param resolvers The TagResolvers to use to replace tags in display name and lore.
         * @deprecated TagResolvers should <b>not be used</b> together with a custom ItemStack!
         * They cause a very resource intensive conversion for each lore line and the display name of the item!
         *
         * @return a new {@link Result} instance.
         */
        @Deprecated
        public static Result of(@Nullable ItemStack itemStack, TagResolver... resolvers) {
            return new Result(itemStack, resolvers);
        }

        /**
         * Creates a new UpdateResult of the specified ItemStack and TagResolver.
         *
         * @param itemStack The ItemStack to render.
         * @param resolver The TagResolver to use to replace tags in display name and lore.
         * @deprecated TagResolvers should <b>not be used</b> together with a custom ItemStack!
         * They cause a very resource intensive conversion for each lore line and the display name of the item!
         * @return a new {@link Result} instance.
         */
        @Deprecated
        public static Result of(@Nullable ItemStack itemStack, TagResolver resolver) {
            return new Result(itemStack, resolver);
        }

        /**
         * Creates a new UpdateResult of the specified ItemStack.
         *
         * @param itemStack The ItemStack to render.
         * @return a new {@link Result} instance.
         */
        public static Result of(@Nullable ItemStack itemStack) {
            return new Result(itemStack);
        }

        public static Result of() {
            return new Result(null);
        }

        public static Result of(TagResolver resolver) {
            return new Result(null, resolver);
        }

        public static Result of(TagResolver... resolvers) {
            return new Result(null, resolvers);
        }

        /**
         * The ItemStack of this result, that is rendered in the inventory.
         *
         * @return the ItemStack of this result.
         */
        public Optional<ItemStack> getCustomStack() {
            return Optional.ofNullable(itemStack);
        }

        /**
         * The {@link TagResolver} to use to replace tags in the display name and lore of the item.
         *
         * @return An array of all the {@link TagResolver}s of this result.
         */
        public Optional<TagResolver> getTagResolver() {
            return Optional.ofNullable(resolver);
        }

        /**
         * The ItemStack of this result, that is rendered in the inventory.
         *
         * @return the ItemStack of this result.
         */
        @Deprecated
        @Nullable
        public ItemStack getItemStack() {
            return itemStack;
        }

        /**
         * The {@link TagResolver}s to use to replace tags in the display name and lore of the item.
         *
         * @return An array of all the {@link TagResolver}s of this result.
         */
        @Deprecated
        public TagResolver[] getResolvers() {
            return new TagResolver[] { resolver };
        }
    }
}
