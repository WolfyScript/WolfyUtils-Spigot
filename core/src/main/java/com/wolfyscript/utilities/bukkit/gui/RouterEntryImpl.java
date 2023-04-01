package com.wolfyscript.utilities.bukkit.gui;

import com.wolfyscript.utilities.common.gui.Component;
import com.wolfyscript.utilities.common.gui.MenuComponent;
import com.wolfyscript.utilities.common.gui.RouterEntry;
import com.wolfyscript.utilities.common.gui.RouterState;

public class RouterEntryImpl implements RouterEntry {

    private final MenuComponent<RouterState> component;
    private final Type type;

    public RouterEntryImpl(MenuComponent<RouterState> component, Type type) {
        this.component = component;
        this.type = type;
    }

    @Override
    public String id() {
        return ((Component) component).getID();
    }

    @Override
    public Type type() {
        return type;
    }

    @Override
    public MenuComponent<RouterState> component() {
        return component;
    }

    public MenuComponent<RouterState> getComponent() {
        return component;
    }
}
