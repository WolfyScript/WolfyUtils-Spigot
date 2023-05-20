package com.wolfyscript.utilities.bukkit.gui;

import com.wolfyscript.utilities.common.WolfyUtils;
import com.wolfyscript.utilities.common.gui.ComponentState;
import com.wolfyscript.utilities.common.gui.GuiViewManagerCommonImpl;
import com.wolfyscript.utilities.common.gui.RenderContext;
import com.wolfyscript.utilities.common.gui.Router;
import com.wolfyscript.utilities.common.gui.Window;
import com.wolfyscript.utilities.common.gui.WindowState;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class GuiViewManagerImpl extends GuiViewManagerCommonImpl {

    private WindowState currentRootState;
    private final Map<Integer, ComponentState> leaveNodes = new HashMap<>();
    private final Map<UUID, RenderContextImpl> viewerContexts = new HashMap<>();

    protected GuiViewManagerImpl(WolfyUtils wolfyUtils, Router rootRouter, Set<UUID> viewers) {
        super(wolfyUtils, rootRouter, viewers);
    }

    Optional<ComponentState> getLeaveNode(int slot) {
        return Optional.ofNullable(leaveNodes.get(slot));
    }

    void updateLeaveNodes(ComponentState state, int... slots) {
        for (int slot : slots) {
            updateLeaveNodes(state, slot);
        }
    }

    void updateLeaveNodes(ComponentState state, int slot) {
        if (state == null) {
            leaveNodes.remove(slot);
        } else {
            leaveNodes.put(slot, state);
        }
    }

    @Override
    public Optional<RenderContext> getRenderContext(UUID viewer) {
        return Optional.ofNullable(viewerContexts.get(viewer));
    }

    @Override
    public void openNew(String... path) {
        Window window = getRoot().open(this, path);
        setCurrentRoot(window);
        if (currentRootState == null) {
            currentRootState = window.createState(this);
        }
        for (UUID viewer : getViewers()) {
            Player player = Bukkit.getPlayer(viewer);
            if (player == null) continue;
            RenderContextImpl context = (RenderContextImpl) window.createContext(this, viewer);
            renderFor(player, context);
        }
    }

    void renderFor(Player player, RenderContextImpl context) {
        if (player.getOpenInventory().getTopInventory() != context.getInventory()) {
            viewerContexts.put(player.getUniqueId(), context);
            player.openInventory(context.getInventory());
        }
    }
}
