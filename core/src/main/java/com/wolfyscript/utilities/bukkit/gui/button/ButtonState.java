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
import com.wolfyscript.utilities.NamespacedKey;
import com.wolfyscript.utilities.bukkit.WolfyUtilsBukkit;
import com.wolfyscript.utilities.bukkit.gui.GuiCluster;
import com.wolfyscript.utilities.bukkit.gui.GuiMenuComponent;
import com.wolfyscript.utilities.bukkit.gui.GuiWindow;
import com.wolfyscript.utilities.bukkit.gui.InventoryAPI;
import com.wolfyscript.utilities.bukkit.gui.cache.CustomCache;
import com.wolfyscript.utilities.bukkit.gui.callback.CallbackButtonAction;
import com.wolfyscript.utilities.bukkit.gui.callback.CallbackButtonPostAction;
import com.wolfyscript.utilities.bukkit.gui.callback.CallbackButtonPreRender;
import com.wolfyscript.utilities.bukkit.gui.callback.CallbackButtonRender;
import com.wolfyscript.utilities.bukkit.world.inventory.ItemUtils;
import java.util.List;
import java.util.function.Consumer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

/**
 * ButtonState represents the state of a Button.
 * <br>
 * It contains the ItemStack and language keys required to render the Item in the inventory.
 * <p>
 * The rendering can be manipulated using the {@link CallbackButtonRender} method that returns the ItemStack that will be rendered.
 * <p>
 * To execute code on a Button click you need to use the {@link CallbackButtonAction} method, which is called each time the button is clicked.
 *
 * @param <C> The type of the {@link CustomCache}
 */
public class ButtonState<C extends CustomCache> {

    public static final String NAME_KEY = ".name";
    public static final String LORE_KEY = ".lore";
    public static final String BUTTON_WINDOW_KEY = "inventories.%s.%s.items.%s";
    public static final String BUTTON_CLUSTER_KEY = "inventories.%s.global_items.%s";

    private WolfyUtilsBukkit wolfyUtilities;
    private String clusterID = null;
    private NamespacedKey windowID = null;
    private final String key;
    private final ItemStack presetIcon;
    private ItemStack icon;
    private CallbackButtonAction<C> action;
    private CallbackButtonRender<C> buttonRender;
    private CallbackButtonPreRender<C> prepareRender;
    private CallbackButtonPostAction<C> postAction;

    public static <C extends CustomCache> Builder<C> of(GuiWindow<C> window, String key) {
        return new Builder<>(window, key);
    }

    public static <C extends CustomCache> Builder<C> of(GuiCluster<C> cluster, String key) {
        return new Builder<>(cluster, key);
    }

    ButtonState(String key, ItemStack presetIcon) {
        Preconditions.checkArgument(key != null && !key.isBlank(), "Cannot create ButtonState with missing key!");
        Preconditions.checkArgument(presetIcon != null, "Cannot create ButtonState with missing icon! Provided icon: " + presetIcon);
        this.key = key;
        this.clusterID = null;
        this.presetIcon = presetIcon;
    }

    ButtonState(NamespacedKey buttonKey, ItemStack presetIcon) {
        Preconditions.checkArgument(buttonKey != null, "Cannot create ButtonState with missing key!");
        Preconditions.checkArgument(presetIcon != null, "Cannot create ButtonState with missing icon! Provided icon: " + presetIcon);
        this.key = buttonKey.getKey();
        this.clusterID = buttonKey.getNamespace();
        this.presetIcon = presetIcon;
    }

    public ItemStack getIcon() {
        return icon.clone();
    }

    public CallbackButtonAction<C> getAction() {
        return action;
    }

    public CallbackButtonRender<C> getRenderAction() {
        return buttonRender;
    }

    public CallbackButtonPreRender<C> getPrepareRender() {
        return prepareRender;
    }

    public CallbackButtonPostAction<C> getPostAction() {
        return postAction;
    }

    /**
     * Initialises the ButtonState for a specific GuiCluster.
     *
     * @param guiMenuComponent The menu component parent to init the state for.
     */
    protected void init(GuiMenuComponent<C> guiMenuComponent) {
        this.wolfyUtilities = guiMenuComponent.getWolfyUtils();
        if (guiMenuComponent instanceof GuiCluster<C> cluster) {
            //For backwards compatibility!
            if (this.clusterID == null) {
                this.clusterID = cluster.getId();
            }
        } else if (guiMenuComponent instanceof GuiWindow<C> window) {
            if (this.windowID == null) {
                this.windowID = window.getNamespacedKey();
            }
        }
        createIcon();
    }

    private void createIcon() {
        if (key != null && !key.isEmpty()) {
            this.icon = ItemUtils.createItem(presetIcon, getName(), getLore());
        }
    }

    protected ItemStack constructIcon() {
        return ItemUtils.createItem(presetIcon, getName(), getLore());
    }

    protected ItemStack constructIcon(TagResolver... tagResolvers) {
        return ItemUtils.createItem(presetIcon, getName(tagResolvers), getLore(tagResolvers));
    }

    protected ItemStack constructCustomIcon(ItemStack itemStack, TagResolver... resolvers) {
        return ItemUtils.createItem(itemStack, getName(resolvers), getLore(resolvers));
    }

    /**
     * Gets the name of this state. The name may vary depending on if it was initiated in a {@link GuiCluster} or {@link GuiWindow}.
     *
     * @return The display name of this state.
     */
    protected Component getName() {
        return getName(TagResolver.empty());
    }

    /**
     * Gets the name of this state. The name may vary depending on if it was initiated in a {@link GuiCluster} or {@link GuiWindow}.
     *
     * @param tagResolvers The resolvers used to handle tags when deserializing the raw value.
     * @return The display name of this state.
     */
    protected Component getName(TagResolver... tagResolvers) {
        if (clusterID != null) {
            return wolfyUtilities.getLanguageAPI().getComponent(String.format(BUTTON_CLUSTER_KEY + NAME_KEY, clusterID, key), tagResolvers);
        }
        return windowID != null ? wolfyUtilities.getLanguageAPI().getComponent(String.format(BUTTON_WINDOW_KEY + NAME_KEY, windowID.getNamespace(), windowID.getKey(), key), tagResolvers) : Component.text("");
    }

    /**
     * Gets the lore of this state. The lore may vary depending on if it was initiated in a {@link GuiCluster} or {@link GuiWindow}.
     *
     * @return The lore (description) of this state.
     */
    protected List<Component> getLore() {
        return getLore(TagResolver.empty());
    }

    /**
     * Gets the lore of this state. The lore may vary depending on if it was initiated in a {@link GuiCluster} or {@link GuiWindow}.
     *
     * @param tagResolvers The resolver used to handle tags when deserializing the raw value.
     * @return The lore (description) of this state.
     */
    protected List<Component> getLore(TagResolver... tagResolvers) {
        if (clusterID != null) {
            return wolfyUtilities.getLanguageAPI().getComponents(String.format(BUTTON_CLUSTER_KEY + LORE_KEY, clusterID, key), tagResolvers);
        }
        return wolfyUtilities.getLanguageAPI().getComponents(String.format(BUTTON_WINDOW_KEY + LORE_KEY, windowID.getNamespace(), windowID.getKey(), key), tagResolvers);
    }

    /**
     * The builder provides an easy solution to create ButtonStates.<br>
     * <p>
     * You can get an instance of this builder via {@link ButtonState#of(GuiWindow, String)} or {@link ButtonState#of(GuiCluster, String)}.<br>
     * It can also be accessed via the button builders:
     * <ul>
     *     <li>{@linkplain ButtonAction.Builder#state(Consumer)}</li>
     *     <li>{@linkplain  ButtonToggle.Builder#enabledState(Consumer)} or {@linkplain ButtonToggle.Builder#disabledState(Consumer)}</li>
     *     <li>{@linkplain ButtonMultipleChoice.Builder#addState(Consumer)}</li>
     * </ul>
     * When the instance is provided via the Button builder, then the default key is equal to the button key.
     * </p>
     *
     * @param <C> The CustomCache type
     */
    public static class Builder<C extends CustomCache> {

        private final WolfyUtilsBukkit api;
        private final InventoryAPI<C> invApi;
        private GuiWindow<C> window;
        private GuiCluster<C> cluster;
        private String key;
        private ItemStack icon;
        private CallbackButtonAction<C> action;
        private CallbackButtonRender<C> render;
        private CallbackButtonPreRender<C> preRender;
        private CallbackButtonPostAction<C> postAction;

        private Builder(GuiWindow<C> window, String key) {
            this(window.getCluster().getInventoryAPI(), window.getWolfyUtils(), key);
            this.window = window;
        }

        private Builder(GuiCluster<C> cluster, String key) {
            this(cluster.getInventoryAPI(), cluster.getWolfyUtils(), key);
            this.cluster = cluster;
        }

        private Builder(InventoryAPI<C> invApi, WolfyUtilsBukkit api, String key) {
            this.api = api;
            this.invApi = invApi;
            this.key = key;
        }

        /**
         * Sets the cluster of the ButtonState.<br>
         * This overrides the previous cluster or window.<br>
         *
         * That can be useful to make use of states from global cluster buttons inside the {@link GuiWindow}.
         *
         * @param cluster The cluster to switch to.
         * @return This button state builder for chaining.
         */
        public Builder<C> cluster(GuiCluster<C> cluster) {
            this.cluster = cluster;
            return this;
        }

        /**
         * Sets the key of the ButtonState (The location in the language where it fetches the name & lore from).
         *
         * @param key The key of the state.
         * @return This button state builder for chaining.
         */
        public Builder<C> key(String key) {
            this.key = key;
            return this;
        }

        /**
         * Sets the key of the ButtonState (The location in the language where it fetches the name & lore from).<br>
         * The NamespacedKey must be from a button in a {@link GuiCluster}. <br>
         * The namespace is equal to the id of the cluster, and the key is equal to the button id.<br>
         *
         * @throws IllegalArgumentException if there is no cluster for the specified namespace.
         * @param buttonKey The namespaced key of the button.
         * @return This button state builder for chaining.
         */
        public Builder<C> key(NamespacedKey buttonKey) {
            String clusterID = buttonKey.getNamespace();
            this.cluster = invApi.getGuiCluster(clusterID);
            Preconditions.checkArgument(this.cluster != null, "Error setting key of ButtonState: Cluster \"" + clusterID + "\" does not exist!");
            this.key(buttonKey.getKey());
            return this;
        }

        /**
         * Appends a sub-key to the end of the current key separated by a '.'.
         * <p>e.g.:
         * <code><pre>ButtonState.of(window, "button_id").subKey("enabled").create();<br></pre></code>
         * (key == "button_id.enabled")
         * </p><br><br>
         * This is useful for multi state buttons, that all have a parent language node with sub nodes for the different states.
         * @param subKey The sub-key to append to the current key.
         * @return This button state builder for chaining.
         */
        public Builder<C> subKey(String subKey) {
            this.key += "." + subKey;
            return this;
        }

        /**
         * Sets the icon of the ButtonState.
         *
         * @param icon The material to use as the icon.
         * @return This button state builder for chaining.
         */
        public Builder<C> icon(ItemStack icon) {
            this.icon = icon;
            return this;
        }

        /**
         * Sets the icon of the ButtonState.
         *
         * @param icon The ItemStack to use as the icon.
         * @return This button state builder for chaining.
         */
        public Builder<C> icon(Material icon) {
            this.icon = new ItemStack(icon);
            return this;
        }

        /**
         * Sets the action callback, that is called when the button is clicked.
         *
         * @param action The action callback.
         * @return This button state for chaining.
         */
        public Builder<C> action(@Nullable CallbackButtonAction<C> action) {
            this.action = action;
            return this;
        }

        /**
         * Sets the render callback, that is called when the button is rendered.
         *
         * @param buttonRender The render callback.
         * @return This button state for chaining.
         */
        public Builder<C> render(@Nullable CallbackButtonRender<C> buttonRender) {
            this.render = buttonRender;
            return this;
        }

        /**
         * Sets the render callback, that is called right before the button is rendered.
         *
         * @param prepareRender The pre-render callback.
         * @return This button state for chaining.
         */
        public Builder<C> preRender(@Nullable CallbackButtonPreRender<C> prepareRender) {
            this.preRender = prepareRender;
            return this;
        }

        /**
         * Sets the action callback, that is called 1 tick after the button was clicked.
         *
         * @param postAction The post-action callback.
         * @return This button state for chaining.
         */
        public Builder<C> postAction(@Nullable CallbackButtonPostAction<C> postAction) {
            this.postAction = postAction;
            return this;
        }

        /**
         * Creates a ButtonState with the previously configured settings.
         *
         * @return A new ButtonState instance with the configured settings.
         */
        public ButtonState<C> create() {
            ButtonState<C> state;
            if (cluster == null) {
                state = new ButtonState<>(key, icon);
            } else {
                state = new ButtonState<>(api.getIdentifiers().getNamespaced(cluster.getId(), key), icon);
            }
            state.prepareRender = preRender;
            state.buttonRender = render;
            state.action = action;
            state.postAction = postAction;
            if (cluster != null) {
                state.init(cluster);
            } else {
                state.init(window);
            }
            return state;
        }

    }
}
