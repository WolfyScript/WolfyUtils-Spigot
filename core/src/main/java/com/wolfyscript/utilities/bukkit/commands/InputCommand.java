package com.wolfyscript.utilities.bukkit.commands;

import me.wolfyscript.utilities.api.WolfyUtilCore;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.gui.GuiHandler;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginIdentifiableCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public final class InputCommand extends Command implements PluginIdentifiableCommand {

    private final WolfyUtilCore core;

    public InputCommand(WolfyUtilCore core) {
        super("wui");
        this.core = core;
        setUsage("/wui <input>");
        setDescription("Input for chat input actions");
    }

    @NotNull
    @Override
    public Plugin getPlugin() {
        return core;
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if (!(sender instanceof Player player)) return true;
        core.getAPIList().parallelStream()
                .filter(WolfyUtilities::hasInventoryAPI)
                .map(wolfyUtilities -> wolfyUtilities.getInventoryAPI().getGuiHandler(player))
                .filter(GuiHandler::isChatEventActive)
                .forEach(guiHandler -> Bukkit.getScheduler().runTask(WolfyUtilities.getWUPlugin(), () -> {
                    //Handles ChatInput
                    if (!guiHandler.onChat(player, String.join(" ", args).trim(), args)) {
                        guiHandler.setChatInputAction(null);
                        guiHandler.openCluster();
                    }
                    if (guiHandler.isChatEventActive()) {
                        guiHandler.cancelChatInput();
                    }
                }));
        return true;
    }

    @NotNull
    @Override
    public List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) throws IllegalArgumentException {
        if (!(sender instanceof Player player)) return List.of();
        return core.getAPIList().stream()
                .filter(WolfyUtilities::hasInventoryAPI)
                .map(wolfyUtilities -> wolfyUtilities.getInventoryAPI().getGuiHandler(player))
                .filter(guiHandler -> guiHandler.isChatEventActive() && guiHandler.hasChatTabComplete())
                .map(guiHandler -> guiHandler.getChatTabComplete().onTabComplete(guiHandler, player, args))
                .filter(Objects::nonNull).findFirst().orElseGet(() -> super.tabComplete(sender, alias, args));
    }
}
