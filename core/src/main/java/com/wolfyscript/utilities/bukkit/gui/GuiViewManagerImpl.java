package com.wolfyscript.utilities.bukkit.gui;

import com.wolfyscript.utilities.common.WolfyUtils;
import com.wolfyscript.utilities.common.gui.*;

import java.util.*;

import com.wolfyscript.utilities.common.gui.impl.AbstractComponentImpl;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class GuiViewManagerImpl extends GuiViewManagerCommonImpl {

    private static long NEXT_ID = Long.MIN_VALUE;

    private final long id;
    private final Map<Integer, Component> leaveNodes = new HashMap<>();
    private final Map<UUID, RenderContextImpl> viewerContexts = new HashMap<>();
    private final Set<SignalledObject> updatedSignalsSinceLastUpdate = new HashSet<>();

    protected GuiViewManagerImpl(WolfyUtils wolfyUtils, Router rootRouter, Set<UUID> viewers) {
        super(wolfyUtils, rootRouter, viewers);
        id = NEXT_ID++;
    }

    Optional<Component> getLeaveNode(int slot) {
        return Optional.ofNullable(leaveNodes.get(slot));
    }

    void updateObjects(Set<SignalledObject> objects) {
        updatedSignalsSinceLastUpdate.addAll(objects);
    }

    public void updateLeaveNodes(Component state, int... slots) {
        for (int slot : slots) {
            updateLeaveNodes(state, slot);
        }
    }

    void updateLeaveNodes(Component state, int slot) {
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
        Window window = getRouter().open(this, path);
        setCurrentRoot(window);
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
            getCurrentMenu().ifPresent(window -> {
                window.construct(this).render((GuiHolder) context.getInventory().getHolder(), this, context);
            });
            return;
        }

        for (SignalledObject signalledObject : updatedSignalsSinceLastUpdate) {
            if (signalledObject instanceof AbstractComponentImpl component) {
                context.enterNode(component);
                for (int slot : component.getSlots()) {
                    context.setSlotOffsetToParent(slot);
                    signalledObject.update(this, (GuiHolder) context.getInventory().getHolder(), context);
                }
            } else {
                signalledObject.update(this, (GuiHolder) context.getInventory().getHolder(), context);
            }
        }
        updatedSignalsSinceLastUpdate.clear();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GuiViewManagerImpl that = (GuiViewManagerImpl) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
