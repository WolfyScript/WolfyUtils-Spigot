package com.wolfyscript.utilities.bukkit;

import com.wolfyscript.utilities.platform.Audiences;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;

import java.util.UUID;

public class AudiencesImpl implements Audiences {

    private final BukkitAudiences audiences;

    public AudiencesImpl(BukkitAudiences audiences) {
        this.audiences = audiences;
    }

    @Override
    public Audience player(UUID uuid) {
        return audiences.player(uuid);
    }

    @Override
    public Audience all() {
        return audiences.all();
    }

    @Override
    public Audience system() {
        return audiences.console();
    }
}
