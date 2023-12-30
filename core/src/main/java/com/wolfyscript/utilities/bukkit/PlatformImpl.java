package com.wolfyscript.utilities.bukkit;

import com.wolfyscript.utilities.WolfyCore;
import com.wolfyscript.utilities.bukkit.gui.GuiUtilsImpl;
import com.wolfyscript.utilities.bukkit.scheduler.SchedulerImpl;
import com.wolfyscript.utilities.bukkit.world.items.ItemsImpl;
import com.wolfyscript.utilities.platform.Audiences;
import com.wolfyscript.utilities.platform.Platform;
import com.wolfyscript.utilities.platform.gui.GuiUtils;
import com.wolfyscript.utilities.platform.scheduler.Scheduler;
import com.wolfyscript.utilities.platform.world.items.Items;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;

public class PlatformImpl implements Platform {

    private BukkitAudiences adventure;
    private final GuiUtils guiUtils = new GuiUtilsImpl();
    private final Scheduler scheduler = new SchedulerImpl();
    private final Items items = new ItemsImpl();
    private Audiences audiences;
    private final WolfyCore core;

    PlatformImpl(WolfyCore core) {
        this.core = core;
    }

    void init() {
        this.adventure = BukkitAudiences.create(((WolfyCoreImpl) core).getPlugin());
        this.audiences = new AudiencesImpl(adventure);
    }

    void unLoad() {
        if (adventure != null) {
            adventure.close();
            adventure = null;
            audiences = null;
        }
    }

    @Override
    public Scheduler scheduler() {
        return scheduler;
    }

    @Override
    public Items items() {
        return items;
    }

    @Override
    public Audiences adventure() {
        if (audiences == null) {
            throw new IllegalStateException("Tried to access Adventure when the plugin was disabled!");
        }
        return audiences;
    }

    @Override
    public GuiUtils guiUtils() {
        return guiUtils;
    }
}
