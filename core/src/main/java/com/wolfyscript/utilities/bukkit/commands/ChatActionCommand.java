package com.wolfyscript.utilities.bukkit.commands;

import com.wolfyscript.utilities.bukkit.chat.ChatImpl;
import java.util.UUID;
import me.wolfyscript.utilities.api.WolfyUtilCore;
import me.wolfyscript.utilities.api.chat.PlayerAction;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginIdentifiableCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class ChatActionCommand extends Command implements PluginIdentifiableCommand {

    private final WolfyUtilCore core;

    public ChatActionCommand(WolfyUtilCore core) {
        super("wua");
        this.core = core;
        setDescription("Used to handle code execution on chat click events.");
    }

    @NotNull
    @Override
    public Plugin getPlugin() {
        return core;
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if (!(sender instanceof Player player)) return true;
        if (args.length > 0) {
            UUID uuid;
            try {
                uuid = UUID.fromString(args[0]);
            } catch (IllegalArgumentException expected) {
                return true;
            }
            PlayerAction action = ChatImpl.getClickData(uuid);
            if (action != null && player.getUniqueId().equals(action.getUuid())) {
                action.run(player);
                if (action.isDiscard()) {
                    ChatImpl.removeClickData(uuid);
                }
            }
        }
        return true;
    }
}
