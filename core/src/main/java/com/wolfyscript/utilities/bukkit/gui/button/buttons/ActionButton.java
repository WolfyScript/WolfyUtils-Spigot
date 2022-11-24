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

package com.wolfyscript.utilities.bukkit.gui.button.buttons;

import com.wolfyscript.utilities.bukkit.gui.GuiCluster;
import com.wolfyscript.utilities.bukkit.gui.GuiHandler;
import com.wolfyscript.utilities.bukkit.gui.GuiUpdate;
import com.wolfyscript.utilities.bukkit.gui.GuiWindow;
import com.wolfyscript.utilities.bukkit.gui.button.Button;
import com.wolfyscript.utilities.bukkit.gui.button.ButtonAction;
import com.wolfyscript.utilities.bukkit.gui.button.ButtonPostAction;
import com.wolfyscript.utilities.bukkit.gui.button.ButtonPreRender;
import com.wolfyscript.utilities.bukkit.gui.button.ButtonRender;
import com.wolfyscript.utilities.bukkit.gui.button.ButtonState;
import com.wolfyscript.utilities.bukkit.gui.button.ButtonType;
import com.wolfyscript.utilities.bukkit.gui.cache.CustomCache;
import java.io.IOException;
import java.util.function.Consumer;
import com.wolfyscript.utilities.bukkit.nms.api.inventory.GUIInventory;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Button that executes the action method and renders the item manipulated via the render method of the ButtonState.
 * <p>
 * action - these methods are executed when the button is clicked.
 * <p>
 * render - these methods are executed when the button is rendered in one of
 * the possible render methods: {@link GuiWindow#onUpdateAsync(GuiUpdate)}, {@link GuiWindow#onUpdateSync(GuiUpdate)}
 * <p>
 * You can set them directly using the constructor and the id of the button is passed into the ButtonState.
 * If the ButtonState requires another key (e.g. when using global item names from lang file) you need to create an ButtonState instance and use
 * {@link ActionButton#ActionButton(String, ButtonState)}
 *
 * @param <C> The type of the {@link CustomCache}
 */
public class ActionButton<C extends CustomCache> extends Button<C> {

    private final String id;
    private final ButtonType type;
    private final ButtonState<C> state;

    /**
     * Constructor for classes that extends the ActionButton.
     * It creates the necessary key, type, and state.
     *
     * @param id The id of the key.
     * @param type The type of the Button.
     * @param state The state of the Button.
     */
    protected ActionButton(String id, ButtonType type, ButtonState<C> state) {
        super(id, type);
        this.id = id;
        this.type = type;
        this.state = state;
    }

    public ActionButton(String id, ButtonState<C> state) {
        this(id, ButtonType.NORMAL, state);
    }

    public ActionButton(String id, ItemStack itemStack) {
        this(id, new ButtonState<>(id, itemStack));
    }

    public ActionButton(String id, ItemStack itemStack, @Nullable ButtonAction<C> action) {
        this(id, itemStack, action, null);
    }

    public ActionButton(String id, ItemStack itemStack, @Nullable ButtonRender<C> render) {
        this(id, itemStack, null, render);
    }

    public ActionButton(String id, ItemStack itemStack, @Nullable ButtonAction<C> action, @Nullable ButtonRender<C> render) {
        this(id, itemStack, action, null, render, null);
    }

    public ActionButton(String id, ItemStack itemStack, @Nullable ButtonAction<C> action, @Nullable ButtonPostAction<C> postAction, @Nullable ButtonRender<C> render, @Nullable ButtonPreRender<C> preRender) {
        this(id, new ButtonState<>(id, itemStack, action, postAction, preRender, render));
    }

    public ActionButton(String id, Material material) {
        this(id, new ButtonState<>(id, material));
    }

    public ActionButton(String id, Material material, @Nullable ButtonAction<C> action) {
        this(id, material, action, null);
    }

    public ActionButton(String id, Material material, @Nullable ButtonRender<C> render) {
        this(id, material, null, render);
    }

    public ActionButton(String id, Material material, @Nullable ButtonAction<C> action, @Nullable ButtonRender<C> render) {
        this(id, new ButtonState<>(id, material, action, render));
    }

    public ActionButton(String id, Material material, @Nullable ButtonAction<C> action, @Nullable ButtonPostAction<C> postAction, @Nullable ButtonRender<C> render, @Nullable ButtonPreRender<C> preRender) {
        this(id, new ButtonState<>(id, material, action, postAction, preRender, render));
    }

    @Override
    public void init(GuiWindow<C> guiWindow) {
        state.init(guiWindow);
    }

    @Override
    public void init(GuiCluster<C> guiCluster) {
        state.init(guiCluster);
    }

    @Override
    public boolean execute(GuiHandler<C> guiHandler, Player player, GUIInventory<C> inventory, int slot, InventoryInteractEvent event) throws IOException {
        if (!type.equals(ButtonType.DUMMY) && state.getAction() != null) {
            return state.getAction().execute(guiHandler.getCustomCache(), guiHandler, player, inventory, this, slot, event);
        }
        return true;
    }

    @Override
    public void postExecute(GuiHandler<C> guiHandler, Player player, GUIInventory<C> inventory, ItemStack itemStack, int slot, InventoryInteractEvent event) throws IOException {
        if (state.getPostAction() != null) {
            state.getPostAction().run(guiHandler.getCustomCache(), guiHandler, player, inventory, itemStack, slot, event);
        }
    }

    @Override
    public void preRender(GuiHandler<C> guiHandler, Player player, GUIInventory<C> inventory, ItemStack itemStack, int slot, boolean help) {
        if (state.getPrepareRender() != null) {
            state.getPrepareRender().prepare(guiHandler.getCustomCache(), guiHandler, player, inventory, itemStack, slot, help);
        }
    }

    @Override
    public void render(GuiHandler<C> guiHandler, Player player, GUIInventory<C> guiInventory, Inventory inventory, ItemStack itemStack, int slot, boolean help) throws IOException {
        applyItem(guiHandler, player, guiInventory, inventory, state, slot, help);
    }

    @Override
    public @NotNull ButtonType getType() {
        return type;
    }

    public ButtonState<C> getState() {
        return state;
    }

    @Override
    public @NotNull String getId() {
        return id;
    }

    /**
     * Abstract builder class to be used in classes that extend the {@link ActionButton}
     *
     * @param <C> The type of the {@link CustomCache}
     * @param <B> The type of the {@link ActionButton} that this builder creates.
     * @param <T> The type of the {@link AbstractBuilder}. A self-reference of this class, so it can return the correct types for chaining.
     */
    public static abstract class AbstractBuilder<C extends CustomCache, B extends ActionButton<C>, T extends AbstractBuilder<C, B, T>> extends Button.Builder<C, B, T> {

        protected ButtonState.Builder<C> stateBuilder;

        protected AbstractBuilder(GuiWindow<C> window, String key, Class<B> buttonType) {
            super(window, key, buttonType);
            this.stateBuilder = ButtonState.of(window, key);
        }

        protected AbstractBuilder(GuiCluster<C> cluster, String key, Class<B> buttonType) {
            super(cluster, key, buttonType);
            this.stateBuilder = ButtonState.of(cluster, key);
        }

        /**
         * Sets the state of the Button, that handles the interactions, icons, etc.
         *
         * @param builderConsumer The ButtonState builder of which the state is constructed.
         * @return This builder to allow for chaining.
         */
        public T state(Consumer<ButtonState.Builder<C>> builderConsumer) {
            builderConsumer.accept(stateBuilder);
            return inst();
        }

    }

    public static class Builder<C extends CustomCache> extends AbstractBuilder<C, ActionButton<C>, Builder<C>> {

        public Builder(GuiWindow<C> window, String key) {
            super(window, key, (Class<ActionButton<C>>)(Object) ActionButton.class);
        }

        public Builder(GuiCluster<C> cluster, String key) {
            super(cluster, key, (Class<ActionButton<C>>)(Object) ActionButton.class);
        }

        @Override
        public ActionButton<C> create() {
            return new ActionButton<>(key, stateBuilder.create());
        }
    }
}
