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

import com.wolfyscript.utilities.bukkit.WolfyUtilsBukkit;
import com.wolfyscript.utilities.bukkit.chat.BukkitChat;
import com.wolfyscript.utilities.bukkit.gui.button.Button;
import com.wolfyscript.utilities.bukkit.gui.button.ButtonAction;
import com.wolfyscript.utilities.bukkit.gui.button.ButtonChatInput;
import com.wolfyscript.utilities.bukkit.gui.button.ButtonDummy;
import com.wolfyscript.utilities.bukkit.gui.button.ButtonItemInput;
import com.wolfyscript.utilities.bukkit.gui.button.ButtonMultipleChoice;
import com.wolfyscript.utilities.bukkit.gui.button.ButtonToggle;
import com.wolfyscript.utilities.bukkit.gui.callback.CallbackChatInput;
import java.util.HashMap;
import java.util.Map;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a GUI menu, that is linked to the InventoryAPI.<br>
 * It is possible to register Buttons and to open the chat for a player to request input.
 * <br>
 * Classes that are GUI menus:
 * <ul>
 *     <li>{@link GuiCluster}</li>
 *     <li>{@link GuiWindow}</li>
 * </ul>
 *
 * @param  The type of the CustomCache
 */
@Deprecated(forRemoval = true)
public abstract class GuiMenuComponent {

    public final WolfyUtilsBukkit wolfyUtilities;
    protected final InventoryAPI inventoryAPI;
    ButtonBuilder buttonBuilder;

    final HashMap<String, Button> buttons = new HashMap<>();

    protected GuiMenuComponent(InventoryAPI inventoryAPI) {
        this.inventoryAPI = inventoryAPI;
        this.wolfyUtilities = inventoryAPI.getWolfyUtils();
    }

    /**
     * Gets the InventoryAPI of this GuiMenu.
     *
     * @return The InventoryAPI
     */
    public InventoryAPI getInventoryAPI() {
        return inventoryAPI;
    }

    /**
     * @return The {@link WolfyUtilsBukkit} that this window belongs to.
     */
    public WolfyUtilsBukkit getWolfyUtils() {
        return wolfyUtilities;
    }

    /**
     * @return The Chat of the API.
     */
    public BukkitChat getChat() {
        return wolfyUtilities.getChat();
    }

    /**
     * Gets the menu specific ButtonBuilder.
     *
     * @return The ButtonBuilder of this menu.
     */
    public ButtonBuilder getButtonBuilder() {
        return buttonBuilder;
    }

    /**
     * @param id The id of the button.
     * @return The button if it exists, else null.
     */
    @Nullable
    public final Button getButton(String id) {
        return buttons.get(id);
    }

    /**
     * @param id The id of the button.
     * @return If the button exists. True if it exists, else false.
     */
    public final boolean hasButton(String id) {
        return buttons.containsKey(id);
    }

    /**
     * Gets all the registered Buttons.<br>
     * For internal use only.
     *
     * @return The map of the registered buttons.
     */
    Map<String, Button> getButtons() {
        return buttons;
    }

    /**
     * Creates a {@link Component} of the specified language key.<br>
     * If the key exists in the language it will be translated and returns the according component.
     * If it is not available it returns an empty component.
     *
     * @param key The key in the language.
     * @return The component set for the key; empty component if not available.
     */
    public Component translatedMsgKey(String key) {
        return translatedMsgKey(key, TagResolver.empty());
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
    public abstract Component translatedMsgKey(String key, TagResolver resolver);

    /**
     * Creates a {@link Component} of the specified language key.<br>
     * If the key exists in the language it will be translated and returns the according component.
     * If it is not available it returns an empty component.
     *
     * @param key The key in the language.
     * @param resolvers The placeholders and values in the message.
     * @return The component set for the key; empty component if not available.
     */
    public Component translatedMsgKey(String key, TagResolver... resolvers) {
        return translatedMsgKey(key, TagResolver.resolver(resolvers));
    }

    /**
     * Opens the chat, send the player the defined message and waits for the input of the player.
     * When the player sends a message the inputAction method is executed.
     *
     * @param guiHandler  The {@link GuiHandler} it should be opened for.
     * @param msg         The message that should be sent to the player.
     * @param inputAction The {@link CallbackChatInput} to be executed when the player types in the chat.
     */
    public void openChat(GuiHandler guiHandler, Component msg, CallbackChatInput inputAction) {
        guiHandler.setChatInputAction(inputAction);
        guiHandler.close();
        getChat().sendMessage(guiHandler.getPlayer(), msg);
    }

    public void sendMessage(GuiHandler guiHandler, Component msg) {
        getChat().sendMessage(guiHandler.getPlayer(), msg);
    }

    /**
     * Interface that contains methods to create new button builders.<br>
     * It is implemented by either {@link GuiWindow} or {@link GuiCluster} and will create the builders accordingly.
     * Calling the {@link Button.Builder#register()} will then register the button either into the {@link GuiWindow} or {@link GuiCluster} depending on from which one the builder was created.
     *
     * @param  The type of the custom cache.
     */
    public interface ButtonBuilder {

        /**
         * Gets a new builder for a {@link ButtonChatInput }.
         *
         * @param id The id of the new button.
         * @return The new builder.
         */
        ButtonChatInput.Builder chatInput(String id);

        /**
         * Gets a new builder for a {@link ButtonAction }.
         *
         * @param id The id of the new button.
         * @return The new builder.
         */
        ButtonAction.Builder action(String id);

        /**
         * Gets a new builder for a {@link ButtonDummy }.
         *
         * @param id The id of the new button.
         * @return The new builder.
         */
        ButtonDummy.Builder dummy(String id);

        /**
         * Gets a new builder for a {@link ButtonItemInput }.
         *
         * @param id The id of the new button.
         * @return The new builder.
         */
        ButtonItemInput.Builder itemInput(String id);

        /**
         * Gets a new builder for a {@link ButtonToggle }.
         *
         * @param id The id of the new button.
         * @return The new builder.
         */
        ButtonToggle.Builder toggle(String id);

        /**
         * Gets a new builder for a {@link ButtonMultipleChoice }.
         *
         * @param id The id of the new button.
         * @return The new builder.
         */
        ButtonMultipleChoice.Builder multiChoice(String id);

    }
}
