package com.wolfyscript.utilities.bukkit.gui.components;

import com.wolfyscript.utilities.common.WolfyUtils;
import com.wolfyscript.utilities.common.gui.Component;
import com.wolfyscript.utilities.common.gui.Position;
import com.wolfyscript.utilities.common.gui.impl.AbstractComponentBuilderImpl;

public abstract class AbstractBukkitComponentBuilderImpl<OWNER extends Component, PARENT extends Component> extends AbstractComponentBuilderImpl<OWNER, PARENT> {

    protected AbstractBukkitComponentBuilderImpl(String id, WolfyUtils wolfyUtils, Position position) {
        super(id, wolfyUtils, position);
    }

}
