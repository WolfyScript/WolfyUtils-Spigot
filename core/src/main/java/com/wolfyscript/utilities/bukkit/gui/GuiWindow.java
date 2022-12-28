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
import com.wolfyscript.utilities.bukkit.BukkitNamespacedKey;
import com.wolfyscript.utilities.bukkit.TagResolverUtil;
import com.wolfyscript.utilities.bukkit.chat.ChatColor;
import com.wolfyscript.utilities.bukkit.chat.ClickAction;
import com.wolfyscript.utilities.bukkit.chat.ClickData;
import com.wolfyscript.utilities.bukkit.chat.IBukkitChat;
import com.wolfyscript.utilities.bukkit.gui.button.Button;
import com.wolfyscript.utilities.bukkit.gui.button.ButtonAction;
import com.wolfyscript.utilities.bukkit.gui.button.ButtonChatInput;
import com.wolfyscript.utilities.bukkit.gui.button.ButtonDummy;
import com.wolfyscript.utilities.bukkit.gui.button.ButtonItemInput;
import com.wolfyscript.utilities.bukkit.gui.button.ButtonMultipleChoice;
import com.wolfyscript.utilities.bukkit.gui.button.ButtonToggle;
import com.wolfyscript.utilities.bukkit.gui.cache.CustomCache;
import com.wolfyscript.utilities.bukkit.gui.callback.CallbackChatInput;
import com.wolfyscript.utilities.bukkit.nms.inventory.InventoryUpdate;
import com.wolfyscript.utilities.tuple.Pair;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import net.kyori.adventure.platform.bukkit.BukkitComponentSerializer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.permissions.Permission;
import org.jetbrains.annotations.Nullable;

/**
 * The GuiWindow represents an Inventory GUI in-game.
 * <p>
 *     The {@link #onInit()} method is used for initialization of the buttons and other data required for the GUI.<br>
 *     To register Buttons you should use the {@link #getButtonBuilder()} and its methods.<br>
 *     The {@link Button.Builder} provides the {@link Button.Builder#register()} to directly register each Button.
 * </p>
 * <p>
 * The methods {@link #onUpdateSync(GuiUpdate)} and {@link #onUpdateAsync(GuiUpdate)} are used to render the window for specific players.<br>
 * {@link GuiUpdate} contains all the required data, like which player it is, the cache of that player and more.
 * This way you can make the GUI contain the specific data.
 * See {@link GuiUpdate} for more information on how to render buttons etc.
 * </p>
 * <p>
 *     To register Buttons
 * </p>
 *
 * @param <C> The type of the {@link CustomCache}.
 */
public abstract class GuiWindow<C extends CustomCache> extends GuiMenuComponent<C> implements Listener {

    private final GuiCluster<C> cluster;
    private final NamespacedKey namespacedKey;
    private boolean forceSyncUpdate;
    private int titleUpdatePeriod = -1;
    private int titleUpdateDelay = 20;
    private final Permission permission;

    //Inventory
    private final InventoryType inventoryType;
    private final int size;

    /**
     * @param cluster The parent {@link GuiCluster} of this window.
     * @param key     The key for this window. This must be comply with the {@link BukkitNamespacedKey} key pattern!
     * @param size    The size of the window. Must be a multiple of 9 and less or equal to 54.
     */
    protected GuiWindow(GuiCluster<C> cluster, String key, int size) {
        this(cluster, key, size, false);
    }

    /**
     * @param cluster         The parent {@link GuiCluster} of this window.
     * @param key             The key for this window. This must be comply with the {@link BukkitNamespacedKey} key pattern!
     * @param size            The size of the window. Must be a multiple of 9 and less or equal to 54.
     * @param forceSyncUpdate If the window should only allow sync code and no async code.
     */
    protected GuiWindow(GuiCluster<C> cluster, String key, int size, boolean forceSyncUpdate) {
        this(cluster, key, null, size, forceSyncUpdate);
    }

    /**
     * @param cluster       The parent {@link GuiCluster} of this window.
     * @param key           The key for this window. This must be comply with the {@link BukkitNamespacedKey} key pattern!
     * @param inventoryType The type of the window.
     */
    protected GuiWindow(GuiCluster<C> cluster, String key, InventoryType inventoryType) {
        this(cluster, key, inventoryType, false);
    }

    /**
     * @param cluster         The parent {@link GuiCluster} of this window.
     * @param key             The key for this window. This must be comply with the {@link BukkitNamespacedKey} key pattern!
     * @param inventoryType   The type of the window.
     * @param forceSyncUpdate If the window should only allow sync code and no async code.
     */
    protected GuiWindow(GuiCluster<C> cluster, String key, InventoryType inventoryType, boolean forceSyncUpdate) {
        this(cluster, key, inventoryType, 0, forceSyncUpdate);
    }

    private GuiWindow(GuiCluster<C> cluster, String key, InventoryType inventoryType, int size, boolean forceSyncUpdate) {
        super(cluster.getInventoryAPI());
        this.cluster = cluster;
        this.namespacedKey = new BukkitNamespacedKey(cluster.getId(), key);
        this.buttonBuilder = new WindowButtonBuilder();
        this.inventoryType = inventoryType;
        this.size = size;
        this.forceSyncUpdate = forceSyncUpdate;
        this.permission = loadPermission();
        Bukkit.getPluginManager().registerEvents(this, wolfyUtilities.getPlugin());
    }

    private Permission loadPermission() {
        var permName = inventoryAPI.getPlugin().getName().toLowerCase(Locale.ROOT) + ".inv." + namespacedKey.toString(".");
        var perm = Bukkit.getPluginManager().getPermission(permName);
        if (perm == null) {
            var parentPermName = inventoryAPI.getPlugin().getName().toLowerCase(Locale.ROOT) + ".inv.*";
            var parentPerm = Bukkit.getPluginManager().getPermission(parentPermName);
            if (parentPerm == null) {
                parentPerm = new Permission(parentPermName);
                parentPerm.addParent(wolfyUtilities.getPermissions().getRootPermission(), true);
                Bukkit.getPluginManager().addPermission(parentPerm);
            }
            var wildcardPermName = inventoryAPI.getPlugin().getName().toLowerCase(Locale.ROOT) + ".inv." + namespacedKey.getNamespace() + ".*";
            var wildcardPerm = new Permission(wildcardPermName);
            wildcardPerm.addParent(parentPerm, true);
            perm = new Permission(permName);
            perm.addParent(wildcardPerm, true);
            Bukkit.getPluginManager().addPermission(perm);
        }
        return perm;
    }

    /**
     * @return The {@link InventoryType} of this window. Representing the type of the inventory.
     */
    public InventoryType getInventoryType() {
        return inventoryType;
    }

    /**
     * @return The size of this window, representing the size of the inventory.
     */
    public int getSize() {
        return size;
    }

    /**
     * This method is called when the inventory is initiated.
     * It's used to register Buttons and optionally other processing on start-up.
     */
    public abstract void onInit();

    /**
     * This method is called each time the gui is updated.
     *
     * @param update The {@link GuiUpdate} instance, that contains all the data of the action that caused this update.
     */
    public abstract void onUpdateSync(GuiUpdate<C> update);

    /**
     * Called each time the title of the window is updated.<br>
     * By default, that only happens once, when the player opens the inventory.<br>
     * Using {@link #setTitleUpdateDelay(int)} and {@link #setTitleUpdatePeriod(int)} you can set the frequency at which the title is updated.<br>
     * When enabled this will be called every specified period.
     *
     * @return The new modified title. Color codes using & will be converted.
     */
    public Component onUpdateTitle(GUIHolder<C> holder) {
        return getInventoryTitle(holder);
    }

    /**
     * This method is called after the {@link #onUpdateSync(GuiUpdate)} is done.
     * It will be run by the scheduler Async, so be careful with using Bukkit methods!
     * Bukkit methods are not Thread safe!
     * <p>
     * If {@link #isForceSyncUpdate()} is enabled then this method is forced to be updated sync too and will act just like {@link #onUpdateSync(GuiUpdate)}!
     *
     * @param update The {@link GuiUpdate} instance, that contains all the data of the action that caused this update.
     */
    public abstract void onUpdateAsync(GuiUpdate<C> update);

    /**
     * This method allows you to execute code when this window is closed and block players from closing the GUI.
     *
     * @return true if the gui close should be cancelled.
     */
    public boolean onClose(GUIHolder<C> holder) {
        return false;
    }

    void create(GuiHandler<C> guiHandler) {
        update(null, guiHandler, null, true);
    }

    void update(Inventory inventory, GuiHandler<C> guiHandler, Map<Integer, Button<C>> postExecuteBtns) {
        update(inventory, guiHandler, postExecuteBtns, false);
    }

    private void update(Inventory inventory, GuiHandler<C> guiHandler, Map<Integer, Button<C>> postExecuteBtns, boolean openInventory) {
        Bukkit.getScheduler().runTask(guiHandler.getWolfyUtils().getPlugin(), () -> {
            GuiUpdate<C> guiUpdate = new GuiUpdate<>(inventory, guiHandler, this);
            guiUpdate.postExecuteButtons(postExecuteBtns);
            callUpdate(guiUpdate, openInventory);
        });
    }

    private void callUpdate(GuiUpdate<C> guiUpdate, boolean openInventory) {
        if (!guiUpdate.getGuiHandler().isChatEventActive()) {
            onUpdateSync(guiUpdate);
            Runnable runnable = () -> openInventory(guiUpdate, openInventory);
            if (forceSyncUpdate) {
                runnable.run();
            } else {
                Bukkit.getScheduler().runTaskAsynchronously(wolfyUtilities.getPlugin(), runnable);
            }
        }
    }

    private void openInventory(GuiUpdate<C> guiUpdate, boolean openInventory) {
        onUpdateAsync(guiUpdate);
        guiUpdate.applyChanges();
        if (openInventory) {
            Bukkit.getScheduler().runTask(wolfyUtilities.getPlugin(), () -> {
                final var guiHandler = guiUpdate.getGuiHandler();
                final var inv = guiUpdate.getInventory();
                guiHandler.setSwitchWindow(true);
                guiHandler.getPlayer().openInventory(inv);
                guiHandler.setSwitchWindow(false);
                if (titleUpdatePeriod > -1) {
                    guiHandler.setWindowUpdateTask(Bukkit.getScheduler().runTaskTimer(wolfyUtilities.getPlugin(), () -> {
                        var player = guiHandler.getPlayer();
                        if (player != null) {
                            InventoryUpdate.updateInventory(wolfyUtilities.getCore().getWolfyUtils().getPlugin(), player, updateTitle(guiUpdate.getGuiHolder()));
                        }
                    }, titleUpdateDelay, titleUpdatePeriod));
                }
            });
        }
    }

    /**
     * The NamespacedKey consists of the namespace and key representing this window.
     * <br>
     * namespace: cluster key
     * <br>
     * key: window key.
     *
     * @return The NamespacedKey of this Window, consisting of the cluster key and this window key.
     */
    public final NamespacedKey getNamespacedKey() {
        return namespacedKey;
    }

    /**
     * Gets the {@link GuiCluster} of this GUI window.
     *
     * @return The parent {@link GuiCluster} of this window.
     */
    public final GuiCluster<C> getCluster() {
        return cluster;
    }

    /**
     * Register a Button to this window.
     * If the id is already in use it will replace the existing button with the new one.
     *
     * @param button The button to register.
     */
    public final void registerButton(Button<C> button) {
        button.init(this);
        buttons.put(button.getId(), button);
    }

    /**
     * Gets the permission required to view this GUI window.
     *
     * @return The permission of this GUI window
     */
    public final Permission getPermission() {
        return permission;
    }

    /**
     * Opens the chat, send the player the defined message and waits for the input of the player.
     * When the player sends a message the inputAction method is executed.
     *
     * @param guiHandler  The {@link GuiHandler} it should be opened for.
     * @param msg         The message that should be sent to the player.
     * @param inputAction The {@link CallbackChatInput} to be executed when the player types in the chat.
     * @deprecated This uses the legacy chat format. <b>Use {@link #openChat(GuiHandler, Component, CallbackChatInput)} instead!</b>
     */
    @Deprecated
    public void openChat(GuiHandler<C> guiHandler, String msg, CallbackChatInput<C> inputAction) {
        guiHandler.setChatInputAction(inputAction);
        guiHandler.close();
        getChat().sendMessage(guiHandler.getPlayer(), msg);
    }

    /**
     * Opens the chat, send the player the defined message, which is set inside of the language under "inventories.&#60;guiCluster&#62;.global_messages.&#60;msgKey&#62;"
     * Then it waits for the player's input.
     * When the player sends the message the inputAction method is executed.
     *
     * @param guiCluster  The {@link GuiCluster} of the message.
     * @param msgKey      The key of the message.
     * @param guiHandler  The {@link GuiHandler} it should be opened for.
     * @param inputAction The {@link CallbackChatInput} to be executed when the player types in the chat.
     * @deprecated This uses the legacy chat format. <b>Use {@link #openChat(GuiHandler, Component, CallbackChatInput)} instead!</b>
     */
    @Deprecated
    public void openChat(GuiCluster<C> guiCluster, String msgKey, GuiHandler<C> guiHandler, CallbackChatInput<C> inputAction) {
        guiHandler.setChatInputAction(inputAction);
        guiHandler.close();
        var chat = wolfyUtilities.getChat();
        chat.sendMessage(guiHandler.getPlayer(), "$inventories." + guiCluster.getId() + ".global_messages." + msgKey + "$");
    }

    /**
     * Opens the chat, send the player the defined message, which is set inside of the language under "inventories.&#60;guiCluster&#62;.&#60;guiWindow&#62;.&#60;msgKey&#62;"
     * Then it waits for the player's input.
     * When the player sends the message the inputAction method is executed
     *
     * @param msgKey      The key of the message.
     * @param guiHandler  the {@link GuiHandler} it should be opened for.
     * @param inputAction The {@link CallbackChatInput} to be executed when the player types in the chat.
     * @deprecated This uses the legacy chat format. <b>Use {@link #openChat(GuiHandler, Component, CallbackChatInput)} instead!</b>
     */
    @Deprecated
    public void openChat(String msgKey, GuiHandler<C> guiHandler, CallbackChatInput<C> inputAction) {
        guiHandler.setChatInputAction(inputAction);
        guiHandler.close();
        getChat().sendKey(guiHandler.getPlayer(), getNamespacedKey(), msgKey);
    }

    /**
     * Opens the chat, send the player the defined action messages and waits for the input of the player.
     * When the player sends the message the inputAction method is executed
     *
     * @param guiHandler  The {@link GuiHandler} it should be opened for.
     * @param clickData   The {@link ClickData} to be send to the player.
     * @param inputAction The {@link CallbackChatInput} to be executed when the player types in the chat.
     * @see #openChat(GuiHandler, Component, CallbackChatInput)
     * @deprecated This uses the legacy chat format. <b>Use {@link #openChat(GuiHandler, Component, CallbackChatInput)} instead!</b> For callback execution on text click use {@link IBukkitChat#executable(Player, boolean, ClickAction)}
     */
    @Deprecated
    public void openActionChat(GuiHandler<C> guiHandler, ClickData clickData, CallbackChatInput<C> inputAction) {
        guiHandler.setChatInputAction(inputAction);
        guiHandler.close();
        getChat().sendActionMessage(guiHandler.getPlayer(), clickData);
    }

    /**
     * Send message to the player without closing the window.
     *
     * @param guiHandler The {@link GuiHandler} this message should be sent to.
     * @param msgKey     The key of the message.
     * @deprecated This uses the legacy chat format. <b>Use {@link #sendMessage(GuiHandler, Component)} instead!</b>
     */
    @Deprecated
    public final void sendMessage(GuiHandler<C> guiHandler, String msgKey) {
        sendMessage(guiHandler.getPlayer(), msgKey);
    }

    /**
     * @param player The Player this message should be sent to.
     * @param msgKey The key of the message.
     * @deprecated This uses the legacy chat format. <b>Use {@link IBukkitChat#sendMessage(Player, Component)} or {@link #sendMessage(GuiHandler, Component)} instead!</b>
     */
    @Deprecated
    public final void sendMessage(Player player, String msgKey) {
        wolfyUtilities.getChat().sendKey(player, getNamespacedKey(), msgKey);
    }

    /**
     * @param guiHandler   The {@link GuiHandler} that this message should be sent to.
     * @param msgKey       The key of the message.
     * @param replacements The replacement strings to replace specific strings with values.
     */
    @SafeVarargs
    @Deprecated
    public final void sendMessage(GuiHandler<C> guiHandler, String msgKey, Pair<String, String>... replacements) {
        wolfyUtilities.getChat().sendKey(guiHandler.getPlayer(), getNamespacedKey(), msgKey, replacements);
    }

    /**
     * @param player       The Player this message should be sent to.
     * @param msgKey       The key of the message.
     * @param replacements The replacement strings to replace specific strings with values.
     */
    @SafeVarargs
    @Deprecated
    public final void sendMessage(Player player, String msgKey, Pair<String, String>... replacements) {
        wolfyUtilities.getChat().sendKey(player, getNamespacedKey(), msgKey, replacements);
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
        return getChat().translated("inventories." + getNamespacedKey().getNamespace() + "." + getNamespacedKey().getKey() + ".messages." + key, resolver);
    }

    /**
     * @return The inventory name of this Window.
     */
    @Deprecated
    protected String getInventoryName() {
        return BukkitComponentSerializer.legacy().serialize(getInventoryTitle(null));
    }

    protected Component getInventoryTitle(GUIHolder<C> holder) {
        return wolfyUtilities.getLanguageAPI().getComponent("inventories." + namespacedKey.getNamespace() + "." + namespacedKey.getKey() + ".gui_name", TagResolverUtil.papi(holder.getPlayer()));
    }

    Component updateTitle(GUIHolder<C> holder) {
        return onUpdateTitle(holder);
    }

    /**
     * @return The help information of this window.
     */
    public List<String> getHelpInformation() {
        List<String> values = new ArrayList<>();
        for (String value : wolfyUtilities.getLanguageAPI().replaceKey("$inventories." + namespacedKey.getNamespace() + "." + namespacedKey.getKey() + ".gui_help$")) {
            values.add(ChatColor.convert(value));
        }
        return values;
    }

    /**
     * ForceSyncUpdate will make sure that no async code is executed on the GUI update
     * and will also open the Inventory one tick after the initial update request, instead of being opened after the async update.
     * <br>
     * It should be enabled when using {@link ButtonItemInput}
     * to make sure that no item could be duplicated, because of tick lag!
     *
     * @return If the forced sync feature is enabled.
     */
    public boolean isForceSyncUpdate() {
        return forceSyncUpdate;
    }

    /**
     * ForceSyncUpdate will make sure that no async code is executed on the GUI update
     * and will also open the Inventory one tick after the initial update request, instead of being opened after the async update.
     * <br>
     * It should be enabled when using {@link ButtonItemInput}
     * to make sure that no item could be duplicated, because of tick lag!
     *
     * @param forceSyncUpdate New forced sync value.
     */
    public void setForceSyncUpdate(boolean forceSyncUpdate) {
        this.forceSyncUpdate = forceSyncUpdate;
    }

    /**
     * Sets the initial delay (in ticks), after which the title is updated.
     *
     * @param titleUpdateDelay The initial delay in ticks.
     */
    public void setTitleUpdateDelay(int titleUpdateDelay) {
        this.titleUpdateDelay = titleUpdateDelay;
    }

    /**
     * Gets the initial delay the update task waits after the inventory was opened.
     *
     * @return The initial delay in ticks.
     */
    public int getTitleUpdateDelay() {
        return titleUpdateDelay;
    }

    /**
     * Sets the period delay (in ticks). The title is updated each period.<br>
     * <b>This can cause flickering of the inventory! The shorter the period, the more noticeable!</b><br>
     * A period delay of -1 will completely disable the update task.
     *
     * @param titleUpdatePeriod The delay between each title update in ticks. Default: -1 = disabled.
     */
    public void setTitleUpdatePeriod(int titleUpdatePeriod) {
        this.titleUpdatePeriod = titleUpdatePeriod;
    }

    /**
     * Gets the current period delay between each title update.<br>
     * A period delay of -1 means that the update task is disabled.
     *
     * @return The delay between each title update. Default: -1
     */
    public int getTitleUpdatePeriod() {
        return titleUpdatePeriod;
    }

    /**
     * The button builder for this GuiWindow. It creates new instances of the builders using the instance of this GuiWindow.<br>
     * Therefor calling the {@link Button.Builder#register()} will then register the button into this GuiWindow.
     */
    protected class WindowButtonBuilder implements ButtonBuilder<C> {

        @Override
        public ButtonChatInput.Builder<C> chatInput(String id) {
            return new ButtonChatInput.Builder<>(GuiWindow.this, id);
        }

        @Override
        public ButtonAction.Builder<C> action(String id) {
            return new ButtonAction.Builder<>(GuiWindow.this, id);
        }

        @Override
        public ButtonDummy.Builder<C> dummy(String id) {
            return new ButtonDummy.Builder<>(GuiWindow.this, id);
        }

        @Override
        public ButtonItemInput.Builder<C> itemInput(String id) {
            return new ButtonItemInput.Builder<>(GuiWindow.this, id);
        }

        @Override
        public ButtonToggle.Builder<C> toggle(String id) {
            return new ButtonToggle.Builder<>(GuiWindow.this, id);
        }

        @Override
        public ButtonMultipleChoice.Builder<C> multiChoice(String id) {
            return new ButtonMultipleChoice.Builder<>(GuiWindow.this, id);
        }
    }

}
