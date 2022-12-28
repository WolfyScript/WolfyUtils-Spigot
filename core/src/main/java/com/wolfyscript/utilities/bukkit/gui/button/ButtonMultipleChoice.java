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
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * This Button goes through each of the States.
 * Each click the index increases by 1 and it goes to the next State.
 * After the index reached the size of the States it is reset to 0 and the first state is selected.
 *
 * @param <C> The type of the {@link CustomCache}
 */
public class ButtonMultipleChoice<C extends CustomCache> extends Button<C> {

    private final List<ButtonState<C>> states;
    private final StateFunction<C> stateFunction;
    private final Map<GuiHandler<C>, Integer> settings;

    /**
     * @param id            The id of the Button
     * @param stateFunction The {@link StateFunction} to set the state of the Button depending on the player, cached data, etc.
     * @param states        The {@link ButtonState}s that this Button will cycle through.
     */
    ButtonMultipleChoice(String id, StateFunction<C> stateFunction, @NotNull List<ButtonState<C>> states) {
        super(id, ButtonType.CHOICES);
        this.states = states;
        settings = new HashMap<>();
        this.stateFunction = stateFunction == null ? ((holder, cache, button, slot) -> settings.getOrDefault(holder.getGuiHandler(), 0)) : stateFunction;
    }

    @Override
    public void init(GuiWindow<C> guiWindow) {
        for (ButtonState<C> btnState : states) {
            initState(btnState, guiWindow);
        }
    }

    @Override
    public void init(GuiCluster<C> guiCluster) {
        for (ButtonState<C> btnState : states) {
            initState(btnState, guiCluster);
        }
    }

    @Override
    public ButtonInteractionResult execute(GUIHolder<C> holder, int slot) throws IOException {
        int setting = settings.getOrDefault(holder.getGuiHandler(), 0);
        if (states != null && setting < states.size()) {
            ButtonState<C> btnState = states.get(setting);
            setting++;
            if (setting >= states.size()) {
                settings.put(holder.getGuiHandler(), 0);
            } else {
                settings.put(holder.getGuiHandler(), setting);
            }
            return btnState.getAction().run(holder, holder.getGuiHandler().getCustomCache(), this, slot, null); // TODO: Details
        }
        return ButtonInteractionResult.def();
    }

    @Override
    public void postExecute(GUIHolder<C> holder, ItemStack itemStack, int slot) throws IOException {
        int setting = settings.computeIfAbsent(holder.getGuiHandler(), g -> 0);
        if (states != null && setting < states.size()) {
            ButtonState<C> btnState = states.get(setting);
            if (btnState.getPostAction() != null) {
                btnState.getPostAction().run(holder, holder.getGuiHandler().getCustomCache(), this, slot, itemStack, null); // TODO: Details
            }
        }
    }

    @Override
    public void preRender(GUIHolder<C> holder, ItemStack itemStack, int slot) {
        int setting = stateFunction.run(holder, holder.getGuiHandler().getCustomCache(), this, slot);
        if (states != null && states.size() > setting && states.get(setting).getPrepareRender() != null) {
            states.get(setting).getPrepareRender().run(holder, holder.getGuiHandler().getCustomCache(), this, slot, itemStack);
        }
    }

    @Override
    public void render(GUIHolder<C> holder, Inventory inventory, int slot) {
        int setting = settings.computeIfAbsent(holder.getGuiHandler(), g -> 0);
        if (states != null && states.size() > setting) {
            applyItem(holder, states.get(setting), slot, inventory);
        }
    }

    public void setState(GuiHandler<C> guiHandler, int state) {
        this.settings.put(guiHandler, state);
    }

    public interface StateFunction<C extends CustomCache> {

        /**
         * Used to set the state for the {@link ButtonMultipleChoice} depending on data from the cache or player, etc.
         *
         * @param cache      The current cache of the GuiHandler
         * @param slot       The slot in which the button is rendered.
         * @return an int indicating the state of the button.
         */
        int run(GUIHolder<C> holder, C cache, ButtonMultipleChoice<C> button, int slot);

    }

    public static class Builder<C extends CustomCache> extends Button.Builder<C, ButtonMultipleChoice<C>, Builder<C>> {

        private final Supplier<ButtonState.Builder<C>> stateBuilderSupplier;
        protected StateFunction<C> stateFunction;
        protected List<ButtonState.Builder<C>> stateBuilders;

        public Builder(GuiWindow<C> window, String id) {
            super(window, id, (Class<ButtonMultipleChoice<C>>) (Object) ButtonMultipleChoice.class);
            stateBuilderSupplier = () -> ButtonState.of(window, id);
            stateFunction = null;
            stateBuilders = new ArrayList<>();
        }

        public Builder(GuiCluster<C> cluster, String id) {
            super(cluster, id, (Class<ButtonMultipleChoice<C>>) (Object) ButtonMultipleChoice.class);
            stateBuilderSupplier = () -> ButtonState.of(cluster, id);
            stateFunction = null;
            stateBuilders = new ArrayList<>();
        }

        /**
         * Adds a choice state to the Button.<br>
         * The Button will toggle between the choices in the order they were added.<br>
         * Once it reaches the end it loops back to the first choice.<br>
         * This behaviour can be manipulated using {@link #stateFunction(StateFunction)}
         *
         * @param builderConsumer The ButtonState builder of which the state is constructed.
         * @return This builder to allow for chaining.
         */
        public Builder<C> addState(Consumer<ButtonState.Builder<C>> builderConsumer) {
            var stateBuilder = stateBuilderSupplier.get();
            builderConsumer.accept(stateBuilder);
            stateBuilders.add(stateBuilder);
            return this;
        }

        /**
         * Sets the state function, that manages which choice is displayed.<br>
         * This allows to display the choice based on current data and state.
         *
         * @param stateFunction The state function to use
         * @return This builder to allow chaining.
         */
        public Builder<C> stateFunction(StateFunction<C> stateFunction) {
            this.stateFunction = stateFunction;
            return this;
        }

        @Override
        public ButtonMultipleChoice<C> create() {
            return new ButtonMultipleChoice<>(key, stateFunction, stateBuilders.stream().map(ButtonState.Builder::create).toList());
        }
    }

}
