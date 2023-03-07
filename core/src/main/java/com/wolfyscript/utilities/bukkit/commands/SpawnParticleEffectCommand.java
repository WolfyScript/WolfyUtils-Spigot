package com.wolfyscript.utilities.bukkit.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import me.wolfyscript.utilities.api.WolfyUtilCore;
import me.wolfyscript.utilities.api.chat.Chat;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.particles.ParticleEffect;
import me.wolfyscript.utilities.util.particles.animators.AnimatorSphere;
import me.wolfyscript.utilities.util.particles.timer.TimerLinear;
import org.bukkit.Particle;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginIdentifiableCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

public final class SpawnParticleEffectCommand extends Command implements PluginIdentifiableCommand {

    private final List<String> COMMANDS = Arrays.asList("spawn", "stop");
    private final WolfyUtilCore core;
    private final Chat chat;

    public SpawnParticleEffectCommand(WolfyUtilCore core) {
        super("particle_effect");
        this.core = core;
        this.chat = (Chat) core.getChat();
        setUsage("/particle_effect spawn");
        setDescription("DEBUG! Spawns a test particle effect on the target block.");
        setPermission("wolfyutilities.command.particle_effect.spawn");
    }

    @NotNull
    @Override
    public Plugin getPlugin() {
        return core;
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if (sender instanceof Player player && testPermission(player)) {
            if (args.length > 0) {
                if (args[0].equalsIgnoreCase("spawn")) {
                    var block = player.getTargetBlockExact(10);
                    if (block != null) {
                        var particleEffect = new ParticleEffect(Particle.FLAME);
                        particleEffect.setKey(NamespacedKey.wolfyutilties("test"));
                        particleEffect.setTimeSupplier(new TimerLinear(0.1, 40));
                        particleEffect.setAnimator(new AnimatorSphere(2));
                        particleEffect.spawn(block);
                    }
                }
            }
        }
        return false;
    }

    @NotNull
    @Override
    public List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) throws IllegalArgumentException {
        List<String> results = new ArrayList<>();
        if (testPermission(sender) && sender instanceof Player player) {
            if (args.length > 1) {
                if (args[0].equalsIgnoreCase("spawn")) {
                    switch (args.length) {
                        case 2 -> {
                            List<String> effects = new ArrayList<>();
                            for (NamespacedKey namespacedKey : core.getRegistries().getParticleEffects().keySet()) {
                                effects.add(namespacedKey.toString());
                            }
                            StringUtil.copyPartialMatches(args[1], effects, results);
                        }
                        case 3 -> {
                            results.add("x");
                            results.add(String.valueOf(player.getLocation().getX()));
                        }
                        case 4 -> {
                            results.add("y");
                            results.add(String.valueOf(player.getLocation().getY()));
                        }
                        case 5 -> {
                            results.add("z");
                            results.add(String.valueOf(player.getLocation().getZ()));
                        }
                        default -> {
                            return results;
                        }
                    }
                }
            } else {
                StringUtil.copyPartialMatches(args[0], COMMANDS, results);
            }
        }
        Collections.sort(results);
        return results;
    }
}
