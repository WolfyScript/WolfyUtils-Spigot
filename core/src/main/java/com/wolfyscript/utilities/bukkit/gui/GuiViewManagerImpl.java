package com.wolfyscript.utilities.bukkit.gui;

import com.wolfyscript.utilities.common.WolfyUtils;
import com.wolfyscript.utilities.common.gui.GuiViewManagerCommonImpl;
import com.wolfyscript.utilities.common.gui.Router;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public class GuiViewManagerImpl extends GuiViewManagerCommonImpl {

    private ComponentStateImpl<?,?> rootStateNode;
    private final Map<Integer, ComponentStateImpl<?,?>> tailStateNodes = new HashMap<>();

    protected GuiViewManagerImpl(WolfyUtils wolfyUtils, Router rootRouter, Set<UUID> viewers) {
        super(wolfyUtils, rootRouter, viewers);
    }

    void changeRootState(ComponentStateImpl<?,?> newState) {
        this.rootStateNode = newState;
    }

    public ComponentStateImpl<?,?> getRootStateNode() {
        return rootStateNode;
    }

    Optional<ComponentStateImpl<?,?>> getTailNode(int slot) {
        return Optional.ofNullable(tailStateNodes.get(slot));
    }
}
