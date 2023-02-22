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

import com.wolfyscript.utilities.common.gui.ButtonInteractionResult;
import com.wolfyscript.utilities.bukkit.gui.GUIHolder;
import com.wolfyscript.utilities.bukkit.gui.GuiCluster;
import com.wolfyscript.utilities.bukkit.gui.GuiWindow;
import com.wolfyscript.utilities.bukkit.gui.cache.CustomCache;
import java.io.IOException;
import java.util.function.Consumer;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

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
 * {@link ButtonAction#ButtonAction(String, ButtonState)}
 *
 * @param  The type of the {@link CustomCache}
 */
public class ButtonAction<C extends CustomCache> extends Button {

    private final String id;
    private final ButtonType type;
    private final ButtonState state;

    /**
     * Constructor for classes that extends the ActionButton.
     * It creates the necessary key, type, and state.
     *
     * @param id The id of the key.
     * @param type The type of the Button.
     * @param state The state of the Button.
     */
    protected ButtonAction(String id, ButtonType type, ButtonState state) {
        super(id, type);
        this.id = id;
        this.type = type;
        this.state = state;
    }

    ButtonAction(String id, ButtonState state) {
        this(id, ButtonType.NORMAL, state);
    }

    @Override
    public void init(GuiWindow guiWindow) {
        initState(state, guiWindow);
    }

    @Override
    public void init(GuiCluster guiCluster) {
        initState(state, guiCluster);
    }

    @Override
    public ButtonInteractionResult execute(GUIHolder holder, int slot) throws IOException {
        if (!type.equals(ButtonType.DUMMY) && state.getAction() != null) {
            return state.getAction().run(holder, holder.getGuiHandler().getCustomCache(), this, slot, null); // TODO: Details
        }
        return ButtonInteractionResult.def();
    }

    @Override
    public void postExecute(GUIHolder holder, ItemStack itemStack, int slot) throws IOException {
        if (state.getPostAction() != null) {
            state.getPostAction().run(holder, holder.getGuiHandler().getCustomCache(), this, slot, itemStack, null); // TODO: Details
        }
    }

    @Override
    public void preRender(GUIHolder holder, ItemStack itemStack, int slot) {
        if (state.getPrepareRender() != null) {
            state.getPrepareRender().run(holder, holder.getGuiHandler().getCustomCache(), this, slot, itemStack);
        }
    }

    @Override
    public void render(GUIHolder holder, Inventory queueInventory, int slot) throws IOException {
        applyItem(holder, state, slot, queueInventory);
    }

    @Override
    public @NotNull ButtonType getType() {
        return type;
    }

    public ButtonState getState() {
        return state;
    }

    @Override
    public @NotNull String getId() {
        return id;
    }

    /**
     * Abstract builder class to be used in classes that extend the {@link ButtonAction}
     *
     * @param  The type of the {@link CustomCache}
     * @param <B> The type of the {@link ButtonAction} that this builder creates.
     * @param <T> The type of the {@link AbstractBuilder}. A self-reference of this class, so it can return the correct types for chaining.
     */
    public static abstract class AbstractBuilder<C extends CustomCache, B extends ButtonAction, T extends AbstractBuilder<C, B, T>> extends Button.Builder<C, B, T> {

        protected ButtonState.Builder stateBuilder;

        protected AbstractBuilder(GuiWindow window, String key, Class<B> buttonType) {
            super(window, key, buttonType);
            this.stateBuilder = ButtonState.of(window, key);
        }

        protected AbstractBuilder(GuiCluster cluster, String key, Class<B> buttonType) {
            super(cluster, key, buttonType);
            this.stateBuilder = ButtonState.of(cluster, key);
        }

        /**
         * Sets the state of the Button, that handles the interactions, icons, etc.
         *
         * @param builderConsumer The ButtonState builder of which the state is constructed.
         * @return This builder to allow for chaining.
         */
        public T state(Consumer<ButtonState.Builder> builderConsumer) {
            builderConsumer.accept(stateBuilder);
            return inst();
        }

    }

    public static class Builder<C extends CustomCache> extends AbstractBuilder<C, ButtonAction, Builder> {

        public Builder(GuiWindow window, String key) {
            super(window, key, (Class<ButtonAction>)(Object) ButtonAction.class);
        }

        public Builder(GuiCluster cluster, String key) {
            super(cluster, key, (Class<ButtonAction>)(Object) ButtonAction.class);
        }

        @Override
        public ButtonAction create() {
            return new ButtonAction<>(key, stateBuilder.create());
        }
    }
}
