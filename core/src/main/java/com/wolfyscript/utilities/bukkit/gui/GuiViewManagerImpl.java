package com.wolfyscript.utilities.bukkit.gui;

import com.wolfyscript.utilities.common.WolfyUtils;
import com.wolfyscript.utilities.common.gui.ComponentState;
import com.wolfyscript.utilities.common.gui.GuiViewManagerCommonImpl;
import com.wolfyscript.utilities.common.gui.components.Router;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class GuiViewManagerImpl extends GuiViewManagerCommonImpl {

    private ComponentState rootStateNode;
    private final Map<Integer, ComponentState> tailStateNodes = new HashMap<>();

    protected GuiViewManagerImpl(WolfyUtils wolfyUtils, Router rootRouter, Set<UUID> viewers) {
        super(wolfyUtils, rootRouter, viewers);
    }

    void changeRootState(ComponentStateImpl<?,?> newState) {
        this.rootStateNode = newState;
    }

    public ComponentState getRootStateNode() {
        return rootStateNode;
    }

    Optional<ComponentState> getTailNode(int slot) {
        return Optional.ofNullable(tailStateNodes.get(slot));
    }

    void updateTailNodes(ComponentState state, int... slots) {
        for (int slot : slots) {
            updateTailNodes(state, slot);
        }
    }

    void updateTailNodes(ComponentState state, int slot) {
        if (state == null) {
            tailStateNodes.remove(slot);
        } else {
            tailStateNodes.put(slot, state);
        }
    }

    @Override
    public void openNew(String... path) {
        for (UUID viewer : getViewers()) {
            Player player = Bukkit.getPlayer(viewer);
            if (player == null) continue;
            Deque<String> pathStack = new ArrayDeque<>(Arrays.asList(path));
            RenderContextImpl context = (RenderContextImpl) getRoot().createContext(this, pathStack, viewer);
            renderFor(player, context);
        }
    }

    void renderFor(Player player, RenderContextImpl context) {
        if (player.getOpenInventory().getTopInventory() != context.getInventory()) {
            player.openInventory(context.getInventory());
        }
        if (rootStateNode == null) {
            rootStateNode = getRoot().createState(null);
        }
        rootStateNode.render((GUIHolder) context.getInventory().getHolder(), context);
    }
}
