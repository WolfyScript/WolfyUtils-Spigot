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

package me.wolfyscript.utilities.api.chat;

import com.wolfyscript.utilities.bukkit.WolfyUtilsBukkit;
import java.util.UUID;
import com.wolfyscript.utilities.bukkit.chat.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

/**
 * Caches all the data necessary to create and call callbacks, when the corresponding text component is clicked.
 *
 * @deprecated <b>Only for internal usages in WolfyUtilities! Going to be made package private in the future!</b>
 */
@Deprecated(since = "3.16.1.0")
public class PlayerAction {

    private UUID uuid;
    private WolfyUtilsBukkit api;

    /**
     * @deprecated No longer used, as the text is now managed by the parent {@link net.kyori.adventure.text.Component}
     */
    @Deprecated(since = "3.16.1.0")
    private TextComponent message;

    private ClickAction clickAction;
    private final boolean discard;

    @Deprecated(since = "3.16.1.0")
    public PlayerAction(WolfyUtilsBukkit api, Player player, ClickData clickData) {
        this.uuid = player.getUniqueId();
        this.api = api;
        this.message = new TextComponent(ChatColor.convert(api.getLanguageAPI().replaceKeys(clickData.getMessage())));
        this.clickAction = clickData.getClickAction();
        this.discard = clickData.isDiscard();
    }

    public PlayerAction(WolfyUtilsBukkit api, Player player, ClickAction action, boolean discard) {
        this.uuid = player.getUniqueId();
        this.api = api;
        this.message = null;
        this.clickAction = action;
        this.discard = discard;
    }

    public void run(Player player) {
        if (clickAction != null) {
            clickAction.run(api, player);
        }
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public WolfyUtilsBukkit getApi() {
        return api;
    }

    public void setApi(WolfyUtilsBukkit api) {
        this.api = api;
    }

    /**
     * @deprecated No longer used, as the text is now managed by the parent {@link net.kyori.adventure.text.Component}
     */
    @Deprecated
    public TextComponent getMessage() {
        return message;
    }

    /**
     * @deprecated No longer used, as the text is now managed by the parent {@link net.kyori.adventure.text.Component}
     */
    @Deprecated
    public void setMessage(TextComponent message) {
        this.message = message;
    }

    public ClickAction getClickAction() {
        return clickAction;
    }

    public void setClickAction(ClickAction clickAction) {
        this.clickAction = clickAction;
    }

    public boolean isDiscard() {
        return discard;
    }
}
