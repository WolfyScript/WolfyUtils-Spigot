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

import com.wolfyscript.utilities.bukkit.chat.IBukkitChat;
import com.wolfyscript.utilities.bukkit.gui.ChatInputAction;
import com.wolfyscript.utilities.bukkit.gui.ChatTabComplete;
import com.wolfyscript.utilities.bukkit.gui.GuiCluster;
import com.wolfyscript.utilities.bukkit.gui.GuiHandler;
import com.wolfyscript.utilities.bukkit.gui.GuiWindow;
import com.wolfyscript.utilities.bukkit.gui.button.ButtonAction;
import com.wolfyscript.utilities.bukkit.gui.button.ButtonRender;
import com.wolfyscript.utilities.bukkit.gui.button.ButtonState;
import com.wolfyscript.utilities.bukkit.gui.cache.CustomCache;
import java.io.IOException;
import me.wolfyscript.utilities.api.chat.ClickData;
import me.wolfyscript.utilities.api.nms.inventory.GUIInventory;
import net.kyori.adventure.platform.bukkit.BukkitComponentSerializer;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.inventory.ItemStack;

/**
 * @param <C> The type of the {@link CustomCache}
 */
public class ChatInputButton<C extends CustomCache> extends ActionButton<C> {

    private ChatInputAction<C> action;
    private ChatTabComplete<C> tabComplete;
    private Component msg = null;
    private final boolean deprecated;
    private ClickData clickData = null;

    @Deprecated
    public ChatInputButton(String id, ButtonState<C> buttonState) {
        super(id, buttonState);
        this.deprecated = false;
    }

    //region Deprecated constructors

    @Deprecated
    public ChatInputButton(String id, ButtonState<C> buttonState, String msg, ChatInputAction<C> action) {
        this(id, buttonState, msg, action, null);
    }

    @Deprecated
    public ChatInputButton(String id, ButtonState<C> buttonState, String msg, ChatInputAction<C> action, ChatTabComplete<C> tabComplete) {
        super(id, buttonState);
        this.action = action;
        this.tabComplete = tabComplete;
        this.msg = BukkitComponentSerializer.legacy().deserialize(msg);
        this.deprecated = true;
    }

    @Deprecated
    public ChatInputButton(String id, ItemStack itemStack, String msg, ChatInputAction<C> action) {
        this(id, new ButtonState<>(id, itemStack), msg, action);
    }

    @Deprecated
    public ChatInputButton(String id, ItemStack itemStack, String msg, ChatInputAction<C> action, ChatTabComplete<C> tabComplete) {
        this(id, new ButtonState<>(id, itemStack), msg, action, tabComplete);
    }

    @Deprecated
    public ChatInputButton(String id, ItemStack itemStack, ButtonAction<C> btnAction, String msg, ChatInputAction<C> action) {
        this(id, new ButtonState<>(id, itemStack, btnAction), msg, action);
    }

    @Deprecated
    public ChatInputButton(String id, ItemStack itemStack, ButtonRender<C> render, String msg, ChatInputAction<C> action) {
        this(id, new ButtonState<>(id, itemStack, render), msg, action);
    }

    @Deprecated
    public ChatInputButton(String id, ItemStack itemStack, ButtonAction<C> btnAction, ButtonRender<C> render, String msg, ChatInputAction<C> action) {
        this(id, new ButtonState<>(id, itemStack, btnAction, render), msg, action);
    }

    @Deprecated
    public ChatInputButton(String id, ButtonState<C> buttonState, ChatInputAction<C> action) {
        this(id, buttonState, action, null);
    }

    @Deprecated
    public ChatInputButton(String id, ButtonState<C> buttonState, ChatInputAction<C> action, ChatTabComplete<C> tabComplete) {
        super(id, buttonState);
        this.action = action;
        this.tabComplete = tabComplete;
        this.deprecated = true;
    }

    @Deprecated
    public ChatInputButton(String id, ItemStack itemStack, ChatInputAction<C> action) {
        this(id, new ButtonState<>(id, itemStack), action);
    }

    @Deprecated
    public ChatInputButton(String id, ItemStack itemStack, ChatInputAction<C> action, ChatTabComplete<C> tabComplete) {
        this(id, new ButtonState<>(id, itemStack), action, tabComplete);
    }

    @Deprecated
    public ChatInputButton(String id, ItemStack itemStack, ButtonAction<C> btnAction, ChatInputAction<C> action) {
        this(id, new ButtonState<>(id, itemStack, btnAction), action);
    }

    @Deprecated
    public ChatInputButton(String id, ItemStack itemStack, ButtonRender<C> render, ChatInputAction<C> action) {
        this(id, new ButtonState<>(id, itemStack, render), action);
    }

    @Deprecated
    public ChatInputButton(String id, ItemStack itemStack, ButtonAction<C> btnAction, ButtonRender<C> render, ChatInputAction<C> action) {
        this(id, new ButtonState<>(id, itemStack, btnAction, render), action);
    }

    @Deprecated
    public ChatInputButton(String id, Material material, ChatInputAction<C> action) {
        this(id, new ButtonState<>(id, material), action);
    }

    @Deprecated
    public ChatInputButton(String id, Material material, ChatInputAction<C> action, ChatTabComplete<C> tabComplete) {
        this(id, new ButtonState<>(id, material), action, tabComplete);
    }

    @Deprecated
    public ChatInputButton(String id, Material material, ButtonAction<C> btnAction, ChatInputAction<C> action) {
        this(id, new ButtonState<>(id, material, btnAction), action);
    }

    @Deprecated
    public ChatInputButton(String id, Material material, ButtonRender<C> render, ChatInputAction<C> action) {
        this(id, new ButtonState<>(id, material, render), action);
    }

    @Deprecated
    public ChatInputButton(String id, Material material, ButtonAction<C> btnAction, ButtonRender<C> render, ChatInputAction<C> action) {
        this(id, new ButtonState<>(id, material, btnAction, render), action);
    }

    @Deprecated
    public ChatInputButton(String id, ButtonState<C> buttonState, ClickData clickData, ChatInputAction<C> action) {
        this(id, buttonState, clickData, action, null);
    }

    @Deprecated
    public ChatInputButton(String id, ButtonState<C> buttonState, ClickData clickData, ChatInputAction<C> chatInput, ChatTabComplete<C> tabComplete) {
        super(id, buttonState);
        this.action = chatInput;
        this.tabComplete = tabComplete;
        this.clickData = clickData;
        this.deprecated = true;
    }

    @Deprecated
    public ChatInputButton(String id, ItemStack itemStack, ClickData clickData, ChatInputAction<C> action) {
        this(id, new ButtonState<>(id, itemStack), clickData, action);
    }

    @Deprecated
    public ChatInputButton(String id, ItemStack itemStack, ButtonRender<C> render, ClickData clickData, ChatInputAction<C> action) {
        this(id, new ButtonState<>(id, itemStack, render), clickData, action);
    }

    @Deprecated
    public ChatInputButton(String id, ItemStack itemStack, ButtonAction<C> btnAction, ClickData clickData, ChatInputAction<C> action) {
        this(id, new ButtonState<>(id, itemStack, btnAction), clickData, action);
    }

    @Deprecated
    public ChatInputButton(String id, ItemStack itemStack, ButtonAction<C> btnAction, ButtonRender<C> render, ClickData clickData, ChatInputAction<C> action) {
        this(id, new ButtonState<>(id, itemStack, btnAction, render), clickData, action);
    }

    @Deprecated
    public ChatInputButton(String id, Material material, ClickData clickData, ChatInputAction<C> action) {
        this(id, new ButtonState<>(id, material), clickData, action);
    }

    @Deprecated
    public ChatInputButton(String id, Material material, ButtonRender<C> render, ClickData clickData, ChatInputAction<C> action) {
        this(id, new ButtonState<>(id, material, render), clickData, action);
    }

    @Deprecated
    public ChatInputButton(String id, Material material, ButtonAction<C> btnAction, ClickData clickData, ChatInputAction<C> action) {
        this(id, new ButtonState<>(id, material, btnAction), clickData, action);
    }

    @Deprecated
    public ChatInputButton(String id, Material material, ButtonAction<C> btnAction, ButtonRender<C> render, ClickData clickData, ChatInputAction<C> action) {
        this(id, new ButtonState<>(id, material, btnAction, render), clickData, action);
    }
    //endregion

    @Override
    public void init(GuiWindow<C> guiWindow) {
        super.init(guiWindow);
        if (msg == null && clickData == null) {
            this.msg = guiWindow.getChat().translated(String.format(ButtonState.BUTTON_WINDOW_KEY + ".message", guiWindow.getCluster().getId(), guiWindow.getNamespacedKey().getKey(), getId()), deprecated);
        }
    }

    @Override
    public void init(GuiCluster<C> guiCluster) {
        super.init(guiCluster);
        if (msg == null && clickData == null) {
            this.msg = guiCluster.getChat().translated(String.format(ButtonState.BUTTON_CLUSTER_KEY + ".message", guiCluster.getId(), getId()), deprecated);
        }
    }

    @Override
    public boolean execute(GuiHandler<C> guiHandler, Player player, GUIInventory<C> inventory, int slot, InventoryInteractEvent event) throws IOException {
        //If the ButtonAction returns true then the ChatInput will be created.
        if (super.execute(guiHandler, player, inventory, slot, event)) {
            guiHandler.setChatTabComplete(tabComplete);
            guiHandler.setChatInputAction(action);
            IBukkitChat chat = guiHandler.getApi().getChat();
            if (msg != null) {
                chat.sendMessage(guiHandler.getPlayer(), msg);
            } else if (clickData != null) {
                chat.sendActionMessage(guiHandler.getPlayer(), clickData);
            }
            guiHandler.close();
        }
        //If the ButtonAction returns false then the ChatInput won't be created.
        return true; //The click is always cancelled.
    }

    public static class Builder<C extends CustomCache> extends AbstractBuilder<C, ChatInputButton<C>, Builder<C>> {

        private ChatInputAction<C> action = null;
        private ChatTabComplete<C> tabComplete = null;
        private Component msg = null;

        public Builder(GuiWindow<C> window, String id) {
            super(window, id, (Class<ChatInputButton<C>>) (Object) ChatInputButton.class);
        }

        public Builder(GuiCluster<C> cluster, String id) {
            super(cluster, id, (Class<ChatInputButton<C>>) (Object) ChatInputButton.class);
        }

        public Builder<C> inputAction(ChatInputAction<C> inputAction) {
            this.action = inputAction;
            return inst();
        }

        public Builder<C> tabComplete(ChatTabComplete<C> tabComplete) {
            this.tabComplete = tabComplete;
            return inst();
        }

        public Builder<C> message(Component msg) {
            this.msg = msg;
            return inst();
        }

        @Override
        public ChatInputButton<C> create() {
            var button = new ChatInputButton<>(key, stateBuilder.create());
            button.msg = msg;
            button.action = action;
            button.tabComplete = tabComplete;
            return button;
        }
    }
}
