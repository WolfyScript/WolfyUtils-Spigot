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

import io.netty.buffer.ByteBuf;
import me.wolfyscript.utilities.api.nms.network.MCByteBuf;
import org.apache.commons.lang3.NotImplementedException;

@Deprecated(since = "4.17", forRemoval = true)
public abstract class NetworkUtil extends UtilComponent {

    protected NetworkUtil(NMSUtil nmsUtil) {
        super(nmsUtil);
    }

    public MCByteBuf buffer(ByteBuf byteBuf) {
        throw new NotImplementedException("No longer supported in 1.20.5+");
    }

    public MCByteBuf buffer() {
        throw new NotImplementedException("No longer supported in 1.20.5+");
    }

}
