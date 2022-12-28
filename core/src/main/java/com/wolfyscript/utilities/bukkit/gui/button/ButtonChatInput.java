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
import com.wolfyscript.utilities.bukkit.gui.callback.CallbackChatInput;
import com.wolfyscript.utilities.bukkit.gui.callback.CallbackChatTabComplete;
import java.io.IOException;
import net.kyori.adventure.text.Component;

/**
 * @param <C> The type of the {@link CustomCache}
 */
public class ButtonChatInput<C extends CustomCache> extends ButtonAction<C> {

    private CallbackChatInput<C> action;
    private CallbackChatTabComplete<C> tabComplete;
    private Component msg = null;
    private final boolean deprecated;

    ButtonChatInput(String id, ButtonState<C> buttonState) {
        super(id, buttonState);
        this.deprecated = false;
    }

    @Override
    public void init(GuiWindow<C> guiWindow) {
        super.init(guiWindow);
        if (msg == null) {
            this.msg = guiWindow.getChat().translated(String.format(ButtonState.BUTTON_WINDOW_KEY + ".message", guiWindow.getCluster().getId(), guiWindow.getNamespacedKey().getKey(), getId()), deprecated);
        }
    }

    @Override
    public void init(GuiCluster<C> guiCluster) {
        super.init(guiCluster);
        if (msg == null) {
            this.msg = guiCluster.getChat().translated(String.format(ButtonState.BUTTON_CLUSTER_KEY + ".message", guiCluster.getId(), getId()), deprecated);
        }
    }

    @Override
    public ButtonInteractionResult execute(GUIHolder<C> holder, int slot) throws IOException {
        //If the ButtonAction returns true then the ChatInput will be created.
        if (!super.execute(holder, slot).isCancelled()) {
            final var guiHandler = holder.getGuiHandler();
            guiHandler.setChatTabComplete(tabComplete);
            guiHandler.setChatInputAction(action);
            if (msg != null) {
                guiHandler.getWolfyUtils().getChat().sendMessage(guiHandler.getPlayer(), msg);
            }
            guiHandler.close();
        }
        //If the ButtonAction returns false then the ChatInput won't be created.
        return ButtonInteractionResult.cancel(true); //The click is always cancelled.
    }

    public static class Builder<C extends CustomCache> extends AbstractBuilder<C, ButtonChatInput<C>, Builder<C>> {

        private CallbackChatInput<C> action = null;
        private CallbackChatTabComplete<C> tabComplete = null;
        private Component msg = null;

        public Builder(GuiWindow<C> window, String id) {
            super(window, id, (Class<ButtonChatInput<C>>) (Object) ButtonChatInput.class);
        }

        public Builder(GuiCluster<C> cluster, String id) {
            super(cluster, id, (Class<ButtonChatInput<C>>) (Object) ButtonChatInput.class);
        }

        public Builder<C> inputAction(CallbackChatInput<C> inputAction) {
            this.action = inputAction;
            return inst();
        }

        public Builder<C> tabComplete(CallbackChatTabComplete<C> tabComplete) {
            this.tabComplete = tabComplete;
            return inst();
        }

        public Builder<C> message(Component msg) {
            this.msg = msg;
            return inst();
        }

        @Override
        public ButtonChatInput<C> create() {
            var button = new ButtonChatInput<>(key, stateBuilder.create());
            button.msg = msg;
            button.action = action;
            button.tabComplete = tabComplete;
            return button;
        }
    }
}
