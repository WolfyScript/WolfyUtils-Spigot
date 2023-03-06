package com.wolfyscript.utilities.bukkit.gui;

import com.wolfyscript.utilities.common.gui.Component;
import com.wolfyscript.utilities.common.gui.MenuComponent;
import com.wolfyscript.utilities.common.gui.RouterEntry;

public class RouterEntryImpl implements RouterEntry {

    private final MenuComponent component;
    private final Type type;

    public RouterEntryImpl(MenuComponent component, Type type) {
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
    public MenuComponent component() {
        return null;
    }

    public MenuComponent getComponent() {
        return component;
    }
}
