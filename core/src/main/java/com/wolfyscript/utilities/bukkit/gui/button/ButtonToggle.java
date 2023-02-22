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
import com.wolfyscript.utilities.bukkit.gui.GuiHandler;
import com.wolfyscript.utilities.bukkit.gui.GuiWindow;
import com.wolfyscript.utilities.bukkit.gui.cache.CustomCache;
import com.wolfyscript.utilities.tuple.Pair;
import java.io.IOException;
import java.util.HashMap;
import java.util.function.Consumer;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This Button toggles between two states and executes the corresponding action!
 * The actions are not allowed to be null!
 * You can add a empty action, but then you should consider using a normal Button!
 *
 * @param  The type of the {@link CustomCache}
 */
public class ButtonToggle<C extends CustomCache> extends Button {

    private final Pair<ButtonState, ButtonState> states;
    private final boolean defaultState;
    private final StateFunction stateFunction;
    private final HashMap<GuiHandler, Boolean> settings;

    /**
     * @param id            The id of the Button
     * @param defaultState  The state to use when the Button is first rendered and haven't been clicked before.
     * @param stateFunction The {@link StateFunction} to set the state of the Button depending on the player, cached data, etc.
     * @param state         The {@link ButtonState} that is rendered if the state is true.
     * @param state2        The {@link ButtonState} that is rendered if the state is false.
     */
    ButtonToggle(String id, boolean defaultState, @Nullable ButtonToggle.StateFunction stateFunction, @NotNull ButtonState state, @NotNull ButtonState state2) {
        super(id, ButtonType.TOGGLE);
        this.defaultState = defaultState;
        states = new Pair<>(state, state2);
        settings = new HashMap<>();
        this.stateFunction = stateFunction == null ? (holder, cache, slot) -> settings.getOrDefault(holder.getGuiHandler(), defaultState) : stateFunction;
    }

    public void setState(GuiHandler guiHandler, boolean enabled) {
        settings.put(guiHandler, enabled);
    }

    public ButtonState getState(GuiHandler guiHandler) {
        return Boolean.TRUE.equals(settings.getOrDefault(guiHandler, defaultState)) ? states.getKey() : states.getValue();
    }

    @Override
    public void init(GuiWindow guiWindow) {
        initState(states.getKey(), guiWindow);
        initState(states.getValue(), guiWindow);
    }

    @Override
    public void init(GuiCluster guiCluster) {
        initState(states.getKey(), guiCluster);
        initState(states.getValue(), guiCluster);
    }

    @Override
    public void postExecute(GUIHolder holder, ItemStack itemStack, int slot) throws IOException {
        ButtonState state = getState(holder.getGuiHandler());
        if (state.getPostAction() != null) {
            state.getPostAction().run(holder, holder.getGuiHandler().getCustomCache(), this, slot, itemStack, null); // TODO: Details
        }
    }

    @Override
    public ButtonInteractionResult execute(GUIHolder holder, int slot) throws IOException {
        ButtonInteractionResult result = getState(holder.getGuiHandler()).getAction().run(holder, holder.getGuiHandler().getCustomCache(),this, slot, null); // TODO: Details
        settings.put(holder.getGuiHandler(), !settings.getOrDefault(holder.getGuiHandler(), defaultState));
        return result;
    }

    @Override
    public void preRender(GUIHolder holder, ItemStack itemStack, int slot) {
        boolean state = stateFunction.run(holder, holder.getGuiHandler().getCustomCache(), slot);
        settings.put(holder.getGuiHandler(), state);
        ButtonState buttonState = state ? states.getKey() : states.getValue();
        if (buttonState.getPrepareRender() != null) {
            buttonState.getPrepareRender().run(holder, holder.getGuiHandler().getCustomCache(), this, slot, itemStack);
        }
    }

    @Override
    public void render(GUIHolder holder, Inventory queueInventory, int slot) {
        ButtonState activeState = getState(holder.getGuiHandler());
        applyItem(holder, activeState, slot, queueInventory);
    }

    public interface StateFunction<C extends CustomCache> {

        /**
         * Used to set the state for the {@link ButtonToggle} depending on data from the cache or player, etc.
         *
         * @param cache      The current cache of the GuiHandler
         * @param slot       The slot in which the button is rendered.
         * @return a boolean indicating the state of the button.
         */
        boolean run(GUIHolder holder, C cache, int slot);

    }

    public static class Builder<C extends CustomCache> extends Button.Builder<C, ButtonToggle, Builder> {

        protected boolean defaultState;
        protected StateFunction stateFunction;
        protected ButtonState.Builder enabledStateBuilder;
        protected ButtonState.Builder disabledStateBuilder;

        public Builder(GuiWindow window, String id) {
            super(window, id, (Class<ButtonToggle>) (Object) ButtonToggle.class);
            this.enabledStateBuilder = ButtonState.of(window, id);
            this.disabledStateBuilder = ButtonState.of(window, id);
        }

        public Builder(GuiCluster cluster, String id) {
            super(cluster, id, (Class<ButtonToggle>) (Object) ButtonToggle.class);
            this.enabledStateBuilder = ButtonState.of(cluster, id);
            this.disabledStateBuilder = ButtonState.of(cluster, id);
        }

        public Builder enabledState(Consumer<ButtonState.Builder> builderConsumer) {
            builderConsumer.accept(enabledStateBuilder);
            return this;
        }

        public Builder disabledState(Consumer<ButtonState.Builder> builderConsumer) {
            builderConsumer.accept(disabledStateBuilder);
            return this;
        }

        public Builder stateFunction(StateFunction stateFunction) {
            this.stateFunction = stateFunction;
            return this;
        }

        public Builder defaultState(boolean defaultState) {
            this.defaultState = defaultState;
            return this;
        }

        @Override
        public ButtonToggle create() {
            return new ButtonToggle<>(key, defaultState, stateFunction, enabledStateBuilder.create(), disabledStateBuilder.create());
        }
    }
}
