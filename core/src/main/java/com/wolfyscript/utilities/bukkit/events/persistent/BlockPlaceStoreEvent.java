package com.wolfyscript.utilities.bukkit.events.persistent;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class BlockPlaceStoreEvent extends Event {

    private static final HandlerList handlers = new HandlerList();




    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
