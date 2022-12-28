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

package com.wolfyscript.utilities.bukkit.gui.button;

import com.google.common.base.Preconditions;
import com.wolfyscript.utilities.bukkit.WolfyUtilsBukkit;
import com.wolfyscript.utilities.common.gui.ButtonInteractionResult;
import com.wolfyscript.utilities.bukkit.gui.GUIHolder;
import com.wolfyscript.utilities.bukkit.gui.GuiCluster;
import com.wolfyscript.utilities.bukkit.gui.GuiMenuComponent;
import com.wolfyscript.utilities.bukkit.gui.GuiWindow;
import com.wolfyscript.utilities.bukkit.gui.InventoryAPI;
import com.wolfyscript.utilities.bukkit.gui.cache.CustomCache;
import com.wolfyscript.utilities.bukkit.gui.callback.CallbackButtonRender;
import java.io.IOException;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * @param <C> The type of the {@link CustomCache}
 */
public abstract class Button<C extends CustomCache> {

    private final String id;
    private final ButtonType type;

    /**
     * Creates a Button with the specified id and type.
     *
     * @param id   The id of the button. Must be unique in the window or cluster.
     * @param type The type of the button. Default: {@link ButtonType#NORMAL}
     */
    protected Button(String id, ButtonType type) {
        Preconditions.checkArgument(id != null && !id.isBlank(), "Button id cannot be null or empty!");
        this.id = id;
        this.type = type == null ? ButtonType.NORMAL : type;
    }

    /**
     * Creates a Button with the specified id.
     *
     * @param id The id of the button. Must be unique in the window or cluster.
     */
    protected Button(String id) {
        this(id, ButtonType.NORMAL);
    }

    /**
     * Called when registered locally inside the {@link GuiWindow#onInit()}.
     *
     * @param guiWindow The {@link GuiWindow} this button is registered in.
     */
    public abstract void init(GuiWindow<C> guiWindow);

    /**
     * Called when registered globally inside the {@link GuiCluster#onInit()}
     *
     * @param guiCluster The {@link GuiCluster} this button is registered in.
     */
    public abstract void init(GuiCluster<C> guiCluster);

    public abstract ButtonInteractionResult execute(GUIHolder<C> guiHandler, int slot) throws IOException;

    public abstract void postExecute(GUIHolder<C> holder, ItemStack itemStack, int slot) throws IOException;

    public abstract void preRender(GUIHolder<C> holder, ItemStack itemStack, int slot);

    public abstract void render(GUIHolder<C> holder, Inventory queueInventory, int slot) throws IOException;

    @NotNull
    public ButtonType getType() {
        return type;
    }

    @NotNull
    public String getId() {
        return id;
    }

    /**
     * Calls the Render Callback of the specified state and applies the result to the specified inventory slot.
     *
     * @param guiHandler
     * @param player
     * @param guiInventory
     * @param inventory
     * @param state
     * @param slot
     */
    protected void applyItem(GUIHolder<C> holder, ButtonState<C> state, int slot, Inventory queueInventory) {
        CallbackButtonRender.Result updateResult = state.getRenderAction().run(holder, holder.getGuiHandler().getCustomCache(), this, slot, state.getIcon());
        queueInventory.setItem(slot, updateResult.getCustomStack()
                .map(itemStack -> state.constructCustomIcon(itemStack, updateResult.getTagResolver().orElseGet(TagResolver::empty)))
                .orElseGet(() -> state.constructIcon(updateResult.getTagResolver().orElseGet(TagResolver::empty)))
        );
    }

    /**
     * Initializes the specified state using the specified parent, to fetch the settings from.
     *
     * @param state The state to initialize.
     * @param guiMenuComponent The parent to fetch the settings from.
     */
    protected void initState(ButtonState<C> state, GuiMenuComponent<C> guiMenuComponent) {
        state.init(guiMenuComponent);
    }

    public static abstract class Builder<C extends CustomCache, B extends Button<C>, T extends Builder<C, B, T>> {

        protected final Class<B> buttonType;
        protected final Class<T> builderType;
        protected final WolfyUtilsBukkit api;
        protected final InventoryAPI<C> invApi;
        protected GuiWindow<C> window;
        protected GuiCluster<C> cluster;
        protected final String key;

        protected Builder(GuiWindow<C> window, String key, Class<B> buttonType) {
            this(window.getCluster().getInventoryAPI(), window.getWolfyUtils(), key, buttonType);
            this.window = window;
        }

        protected Builder(GuiCluster<C> cluster, String key, Class<B> buttonType) {
            this(cluster.getInventoryAPI(), cluster.getWolfyUtils(), key, buttonType);
            this.cluster = cluster;
        }

        private Builder(InventoryAPI<C> invApi, WolfyUtilsBukkit api, String key, Class<B> buttonType) {
            this.api = api;
            this.invApi = invApi;
            this.buttonType = buttonType;
            this.key = key;
            this.builderType = (Class<T>) getClass();
        }

        /**
         * Constructs the Button instance with the builders' settings and returns it.
         *
         * @return The instance of the newly created Button.
         */
        public abstract B create();

        /**
         * Constructs the Button instance with the builders' settings and registers it to the parent GuiComponent.
         */
        public void register() {
            B button = create();
            if (window != null) {
                window.registerButton(button);
            } else if (cluster != null) {
                cluster.registerButton(button);
            }
        }

        protected T inst() {
            return builderType.cast(this);
        }

    }
}
