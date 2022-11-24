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
import com.wolfyscript.utilities.bukkit.BukkitNamespacedKey;
import com.wolfyscript.utilities.bukkit.WolfyUtilsBukkit;
import com.wolfyscript.utilities.bukkit.gui.GuiCluster;
import com.wolfyscript.utilities.bukkit.gui.GuiWindow;
import com.wolfyscript.utilities.bukkit.gui.InventoryAPI;
import com.wolfyscript.utilities.bukkit.gui.cache.CustomCache;
import java.util.List;
import java.util.function.Consumer;
import com.wolfyscript.utilities.bukkit.world.inventory.ItemUtils;
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
 * The rendering can be manipulated using the {@link ButtonRender} method that returns the ItemStack that will be rendered.
 * <p>
 * To execute code on a Button click you need to use the {@link ButtonAction} method, which is called each time the button is clicked.
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
    private BukkitNamespacedKey windowID = null;
    private String key;
    private final ItemStack presetIcon;
    private ItemStack icon;
    private ButtonAction<C> action;
    private ButtonRender<C> buttonRender;
    private ButtonPreRender<C> prepareRender;
    private ButtonPostAction<C> postAction;

    public static <C extends CustomCache> Builder<C> of(GuiWindow<C> window, String key) {
        return new Builder<>(window, key);
    }

    public static <C extends CustomCache> Builder<C> of(GuiCluster<C> cluster, String key) {
        return new Builder<>(cluster, key);
    }

    @Deprecated
    public ButtonState(String key, ItemStack presetIcon) {
        Preconditions.checkArgument(key != null && !key.isBlank(), "Cannot create ButtonState with missing key!");
        Preconditions.checkArgument(presetIcon != null, "Cannot create ButtonState with missing icon! Provided icon: " + presetIcon);
        this.key = key;
        this.clusterID = null;
        this.presetIcon = presetIcon;
    }

    @Deprecated
    public ButtonState(String key, Material presetIcon) {
        this(key, new ItemStack(presetIcon));
    }

    @Deprecated
    public ButtonState(NamespacedKey buttonKey, ItemStack presetIcon) {
        Preconditions.checkArgument(buttonKey != null, "Cannot create ButtonState with missing key!");
        Preconditions.checkArgument(presetIcon != null, "Cannot create ButtonState with missing icon! Provided icon: " + presetIcon);
        this.key = buttonKey.getKey();
        this.clusterID = buttonKey.getNamespace();
        this.presetIcon = presetIcon;
    }

    @Deprecated
    public ButtonState(NamespacedKey buttonKey, Material presetIcon) {
        this(buttonKey, new ItemStack(presetIcon));
    }

    public void init(GuiCluster<C> cluster) {
        this.wolfyUtilities = cluster.getWolfyUtilities();
        //For backwards compatibility!
        if (this.clusterID == null) {
            this.clusterID = cluster.getId();
        }
        createIcon(null);
    }

    public void init(GuiWindow<C> window) {
        this.wolfyUtilities = window.wolfyUtilities;
        if (this.windowID == null) {
            this.windowID = window.getNamespacedKey();
        }
        createIcon(window);
    }

    public ItemStack getIcon() {
        return icon.clone();
    }

    private void createIcon(GuiWindow<C> window) {
        if (key != null && !key.isEmpty()) {
            this.icon = ItemUtils.createItem(presetIcon, getName(window), getLore(window));
        }
    }

    public ItemStack constructIcon() {
        return ItemUtils.createItem(presetIcon, getName(), getLore());
    }

    public ItemStack constructIcon(TagResolver tagResolver) {
        return ItemUtils.createItem(presetIcon, getName(tagResolver), getLore(tagResolver));
    }

    /**
     * Gets the name of this state. The name may vary depending on if it was initiated in a {@link GuiCluster} or {@link GuiWindow}.
     *
     * @return The display name of this state.
     */
    public Component getName() {
        return getName(TagResolver.empty());
    }

    /**
     * Gets the name of this state. The name may vary depending on if it was initiated in a {@link GuiCluster} or {@link GuiWindow}.
     *
     * @param tagResolver The resolver used to handle tags when deserializing the raw value.
     * @return The display name of this state.
     */
    public Component getName(TagResolver tagResolver) {
        if (clusterID != null) {
            return wolfyUtilities.getLanguageAPI().getComponent(String.format(BUTTON_CLUSTER_KEY + NAME_KEY, clusterID, key), tagResolver);
        }
        return windowID != null ? wolfyUtilities.getLanguageAPI().getComponent(String.format(BUTTON_WINDOW_KEY + NAME_KEY, windowID.getNamespace(), windowID.getKey(), key), tagResolver) : Component.text("");
    }

    @Deprecated
    public Component getName(GuiWindow<C> window) {
        return getName(window, List.of());
    }

    @Deprecated
    public Component getName(GuiWindow<C> window, List<? extends TagResolver> templates) {
        if (clusterID != null) {
            return wolfyUtilities.getLanguageAPI().getComponent(String.format(BUTTON_CLUSTER_KEY + NAME_KEY, clusterID, key), true, templates);
        }
        return wolfyUtilities.getLanguageAPI().getComponent(String.format(BUTTON_WINDOW_KEY + NAME_KEY, window.getNamespacedKey().getNamespace(), window.getNamespacedKey().getKey(), key), true, templates);
    }

    /**
     * Gets the lore of this state. The lore may vary depending on if it was initiated in a {@link GuiCluster} or {@link GuiWindow}.
     *
     * @return The lore (description) of this state.
     */
    public List<Component> getLore() {
        return getLore(TagResolver.empty());
    }

    /**
     * Gets the lore of this state. The lore may vary depending on if it was initiated in a {@link GuiCluster} or {@link GuiWindow}.
     *
     * @param tagResolver The resolver used to handle tags when deserializing the raw value.
     * @return The lore (description) of this state.
     */
    public List<Component> getLore(TagResolver tagResolver) {
        if (clusterID != null) {
            return wolfyUtilities.getLanguageAPI().getComponents(String.format(BUTTON_CLUSTER_KEY + LORE_KEY, clusterID, key), tagResolver);
        }
        return wolfyUtilities.getLanguageAPI().getComponents(String.format(BUTTON_WINDOW_KEY + LORE_KEY, windowID.getNamespace(), windowID.getKey(), key), tagResolver);
    }

    @Deprecated
    public List<Component> getLore(GuiWindow<C> window) {
        return getLore(window, List.of());
    }

    @Deprecated
    public List<Component> getLore(GuiWindow<C> window, List<? extends TagResolver> templates) {
        if (clusterID != null) {
            return wolfyUtilities.getLanguageAPI().getComponents(String.format(BUTTON_CLUSTER_KEY + LORE_KEY, clusterID, key), true, templates);
        }
        return wolfyUtilities.getLanguageAPI().getComponents(String.format(BUTTON_WINDOW_KEY + LORE_KEY, window.getNamespacedKey().getNamespace(), window.getNamespacedKey().getKey(), key), true, templates);
    }

    public ButtonAction<C> getAction() {
        return action;
    }

    @Deprecated
    public ButtonState<C> setAction(ButtonAction<C> action) {
        this.action = action;
        return this;
    }

    public ButtonRender<C> getRenderAction() {
        return buttonRender;
    }

    @Deprecated
    public ButtonState<C> setRenderAction(ButtonRender<C> renderAction) {
        this.buttonRender = renderAction;
        return this;
    }

    public ButtonPreRender<C> getPrepareRender() {
        return prepareRender;
    }

    @Deprecated
    public ButtonState<C> setPrepareRender(ButtonPreRender<C> prepareRender) {
        this.prepareRender = prepareRender;
        return this;
    }

    public ButtonPostAction<C> getPostAction() {
        return postAction;
    }

    @Deprecated
    public ButtonState<C> setPostAction(ButtonPostAction<C> postAction) {
        this.postAction = postAction;
        return this;
    }

    /**
     * The builder provides an easy solution to create ButtonStates.<br>
     * <p>
     * You can get an instance of this builder via {@link ButtonState#of(GuiWindow, String)} or {@link ButtonState#of(GuiCluster, String)}.<br>
     * It can also be accessed via the button builders:
     * <ul>
     *     <li>{@linkplain com.wolfyscript.utilities.bukkit.gui.button.buttons.ActionButton.Builder#state(Consumer)}</li>
     *     <li>{@linkplain  com.wolfyscript.utilities.bukkit.gui.button.buttons.ToggleButton.Builder#enabledState(Consumer)} or {@linkplain com.wolfyscript.utilities.bukkit.gui.button.buttons.ToggleButton.Builder#disabledState(Consumer)}</li>
     *     <li>{@linkplain com.wolfyscript.utilities.bukkit.gui.button.buttons.MultipleChoiceButton.Builder#addState(Consumer)}</li>
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
        private ButtonAction<C> action;
        private ButtonRender<C> render;
        private ButtonPreRender<C> preRender;
        private ButtonPostAction<C> postAction;

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
        public Builder<C> key(BukkitNamespacedKey buttonKey) {
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
        public Builder<C> action(@Nullable ButtonAction<C> action) {
            this.action = action;
            return this;
        }

        /**
         * Sets the render callback, that is called when the button is rendered.
         *
         * @deprecated Use the new {@link #render(CallbackButtonRender)} instead! The new callback provides a better solution for tags.
         * @param buttonRender The render callback.
         * @return This button state for chaining.
         */
        @Deprecated
        public Builder<C> render(@Nullable ButtonRender<C> buttonRender) {
            this.render = buttonRender;
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
        public Builder<C> preRender(@Nullable ButtonPreRender<C> prepareRender) {
            this.preRender = prepareRender;
            return this;
        }

        /**
         * Sets the action callback, that is called 1 tick after the button was clicked.
         *
         * @param postAction The post-action callback.
         * @return This button state for chaining.
         */
        public Builder<C> postAction(@Nullable ButtonPostAction<C> postAction) {
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
