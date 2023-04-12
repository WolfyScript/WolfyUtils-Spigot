package com.wolfyscript.utilities.bukkit.gui;

import com.wolfyscript.utilities.NamespacedKey;
import com.wolfyscript.utilities.common.WolfyUtils;
import com.wolfyscript.utilities.common.gui.Component;
import com.wolfyscript.utilities.common.gui.ComponentBuilder;

public abstract class AbstractBukkitComponentBuilder<OWNER extends Component, PARENT extends Component> implements ComponentBuilder<OWNER, PARENT> {

    private final NamespacedKey type;
    private final String id;
    private final WolfyUtils wolfyUtils;

    protected AbstractBukkitComponentBuilder(String id, WolfyUtils wolfyUtils) {
        this.type = wolfyUtils.getIdentifiers().getNamespaced(getClass());
        this.id = id;
        this.wolfyUtils = wolfyUtils;
    }

    @Override
    public String getID() {
        return id;
    }

    protected WolfyUtils getWolfyUtils() {
        return wolfyUtils;
    }

    @Override
    public NamespacedKey getNamespacedKey() {
        return type;
    }
}
