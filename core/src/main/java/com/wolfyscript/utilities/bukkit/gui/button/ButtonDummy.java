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
import java.io.IOException;

/**
 * This Button acts as a dummy, it will not run the action, even if you set one for the ButtonState!
 *
 * @param  The type of the {@link CustomCache}
 */
public class ButtonDummy<C extends CustomCache> extends ButtonAction {

    ButtonDummy(String id, ButtonState state) {
        super(id, ButtonType.DUMMY, state);
    }

    @Override
    public ButtonInteractionResult execute(GUIHolder holder, int slot) throws IOException {
        return ButtonInteractionResult.cancel(true); // This is a dummy button. Always cancel the interaction!
    }

    public static class Builder<C extends CustomCache> extends AbstractBuilder<C, ButtonDummy, Builder> {

        public Builder(GuiWindow window, String id) {
            super(window, id, (Class<ButtonDummy>) (Object) ButtonDummy.class);
        }

        public Builder(GuiCluster cluster, String id) {
            super(cluster, id, (Class<ButtonDummy>) (Object) ButtonDummy.class);
        }

        @Override
        public ButtonDummy create() {
            return new ButtonDummy<>(key, stateBuilder.create());
        }
    }

}
