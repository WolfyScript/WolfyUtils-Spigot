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

package me.wolfyscript.utilities.api.nms;

import com.wolfyscript.utilities.paper.WolfyCorePaper;
import de.tr7zw.nbtapi.NBT;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Deprecated(forRemoval = true, since = "4.17")
public abstract class ItemUtil extends UtilComponent {

    protected ItemUtil(NMSUtil nmsUtil) {
        super(nmsUtil);
    }

    /**
     * Converts an {@link org.bukkit.inventory.ItemStack} to a Json string
     * for sending with {@link net.md_5.bungee.api.chat.BaseComponent}'s.
     * Or to save it in the vanilla style Json String.
     *
     * @param itemStack the item to convert
     * @return the Json string representation of the item in NMS style.
     */
    public String getItemStackJson(ItemStack itemStack) {
        return NBT.itemStackToNBT(itemStack).toString();
    }

    /**
     * Converts the NMS Json Sting to an {@link org.bukkit.inventory.ItemStack}.
     *
     * @param json the NMS json to convert
     * @return the ItemStack representation of the Json String
     */
    public org.bukkit.inventory.ItemStack getJsonItemStack(String json) {
        return NBT.itemStackFromNBT(NBT.parseNBT(json));
    }

    public String getItemStackBase64(org.bukkit.inventory.ItemStack itemStack) throws IOException{
        if (itemStack == null) return "null";
        if (getNmsUtil().getWolfyUtilities().getCore() instanceof WolfyCorePaper) {
            byte[] bytes = itemStack.serializeAsBytes();
            return Base64.getEncoder().encodeToString(bytes);
        }
        byte[] bytes = getItemStackJson(itemStack).getBytes(StandardCharsets.UTF_8);
        return Base64.getEncoder().encodeToString(bytes);
    }

    public org.bukkit.inventory.ItemStack getBase64ItemStack(String data) throws IOException {
        return getBase64ItemStack(Base64.getDecoder().decode(data));
    }

    public org.bukkit.inventory.ItemStack getBase64ItemStack(byte[] bytes) throws IOException {
        if (bytes == null || bytes.length == 0) return null;
        var json = new String(bytes);
        return getJsonItemStack(json);
    }
}
