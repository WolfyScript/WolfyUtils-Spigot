package com.wolfyscript.utilities.bukkit;

public final class WolfyCoreBukkitBootstrap extends WolfyCoreBootstrap {

    private final WolfyCoreBukkit core;

    public WolfyCoreBukkitBootstrap() {
        super();
        this.core = new WolfyCoreBukkit(this);
    }

    public WolfyCoreBukkit getCore() {
        return core;
    }

}
