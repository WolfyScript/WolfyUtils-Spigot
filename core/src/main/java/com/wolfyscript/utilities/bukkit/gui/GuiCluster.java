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

package com.wolfyscript.utilities.bukkit.gui;

import com.wolfyscript.utilities.NamespacedKey;
import com.wolfyscript.utilities.bukkit.gui.button.Button;
import com.wolfyscript.utilities.bukkit.gui.button.ButtonAction;
import com.wolfyscript.utilities.bukkit.gui.button.ButtonChatInput;
import com.wolfyscript.utilities.bukkit.gui.button.ButtonDummy;
import com.wolfyscript.utilities.bukkit.gui.button.ButtonItemInput;
import com.wolfyscript.utilities.bukkit.gui.button.ButtonMultipleChoice;
import com.wolfyscript.utilities.bukkit.gui.button.ButtonToggle;
import com.wolfyscript.utilities.bukkit.gui.cache.CustomCache;
import java.util.HashMap;
import java.util.Map;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;

/**
 * The GuiCluster combines multiple child <b>{@link GuiWindow}s</b>.<br>
 * You can think of it as a namespace of GuiWindows.<br>
 *
 * Buttons registered in a GuiCluster are available globally, so you can use them in the child windows or access them in windows outside this cluster.<br>
 *
 * @param  The type of the CustomCache
 */
@Deprecated(forRemoval = true)
public abstract class GuiCluster extends GuiMenuComponent {

    protected final InventoryAPI inventoryAPI;
    private final String id;
    private final Map<String, GuiWindow> guiWindows;

    private NamespacedKey entry;

    protected GuiCluster(InventoryAPI inventoryAPI, String id) {
        super(inventoryAPI);
        this.inventoryAPI = inventoryAPI;
        this.id = id;
        this.guiWindows = new HashMap<>();
        this.entry = null;
        this.buttonBuilder = new ClusterButtonBuilder();
    }

    /**
     * This method is called when the cluster is initialized.
     */
    public abstract void onInit();

    /**
     * Gets the entrypoint of this cluster.
     *
     * @return The namespaced key of the entrypoint window.
     */
    public NamespacedKey getEntry() {
        return entry;
    }

    /**
     * Registers the button in this cluster.
     *
     * @param button The button to register.
     */
    public void registerButton(Button button) {
        button.init(this);
        buttons.putIfAbsent(button.getId(), button);
    }

    /**
     * Registers a {@link GuiWindow} in this cluster.<br>
     * In case the entrypoint isn't set at the time of the registration, it'll set this window as the entry.
     *
     * @param guiWindow The GuiWindow to register.
     */
    protected void registerGuiWindow(GuiWindow guiWindow) {
        if (this.entry == null) {
            this.entry = guiWindow.getNamespacedKey();
        }
        guiWindow.onInit();
        guiWindows.put(guiWindow.getNamespacedKey().getKey(), guiWindow);
    }

    /**
     * Gets the child {@link GuiWindow} by its id.
     *
     * @param id The id of the child window.
     * @return The GuiWindow of the id; otherwise null if there is no GuiWindow for that id.
     */
    public GuiWindow getGuiWindow(String id) {
        return guiWindows.get(id);
    }

    /**
     * Gets the id of the GuiCluster.
     *
     * @return The id of the cluster.
     */
    public String getId() {
        return id;
    }

    Map<String, GuiWindow> getGuiWindows() {
        return guiWindows;
    }

    /**
     * Creates a {@link Component} of the specified language key.<br>
     * If the key exists in the language it will be translated and returns the according component.
     * If it is not available it returns an empty component.
     *
     * @param key The key in the language.
     * @param resolver The placeholders and values in the message.
     * @return The component set for the key; empty component if not available.
     */
    @Override
    public Component translatedMsgKey(String key, TagResolver resolver) {
        return getChat().translated("inventories." + id + ".global_messages." + key, resolver);
    }

    /**
     * The button builder for this GuiCluster. It creates new instances of the builders using the instance of this GuiCluster.<br>
     * Therefor calling the {@link Button.Builder#register()} will then register the button into this GuiCluster.
     */
    protected class ClusterButtonBuilder implements ButtonBuilder {

        @Override
        public ButtonChatInput.Builder chatInput(String id) {
            return new ButtonChatInput.Builder<>(GuiCluster.this, id);
        }

        @Override
        public ButtonAction.Builder action(String id) {
            return new ButtonAction.Builder<>(GuiCluster.this, id);
        }

        @Override
        public ButtonDummy.Builder dummy(String id) {
            return new ButtonDummy.Builder<>(GuiCluster.this, id);
        }

        @Override
        public ButtonItemInput.Builder itemInput(String id) {
            return new ButtonItemInput.Builder<>(GuiCluster.this, id);
        }

        @Override
        public ButtonToggle.Builder toggle(String id) {
            return new ButtonToggle.Builder<>(GuiCluster.this, id);
        }

        @Override
        public ButtonMultipleChoice.Builder multiChoice(String id) {
            return new ButtonMultipleChoice.Builder<>(GuiCluster.this, id);
        }
    }
}
