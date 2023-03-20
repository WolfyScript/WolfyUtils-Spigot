package com.wolfyscript.utilities.bukkit.gui;

import com.wolfyscript.utilities.common.gui.Component;

public interface ComponentBuilder<COMPONENT extends Component, PARENT extends Component> {

    COMPONENT create(PARENT parent);
}
