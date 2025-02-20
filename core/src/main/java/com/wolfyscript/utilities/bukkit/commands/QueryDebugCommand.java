package com.wolfyscript.utilities.bukkit.commands;

import com.wolfyscript.utilities.bukkit.nbt.NBTQuery;
import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTItem;
import me.wolfyscript.utilities.api.WolfyUtilCore;
import me.wolfyscript.utilities.util.inventory.ItemUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginIdentifiableCommand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public final class QueryDebugCommand extends Command implements PluginIdentifiableCommand {

    private final WolfyUtilCore core;

    public QueryDebugCommand(WolfyUtilCore core) {
        super("query_item");
        this.core = core;
        setUsage("/query_item");
        setPermission("wolfyutilities.command.query_debug");
    }

    @NotNull
    @Override
    public Plugin getPlugin() {
        return core;
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if (!(sender instanceof Player player) || !testPermission(sender)) return true;
        ItemStack stack = player.getEquipment().getItem(EquipmentSlot.HAND);
        if (!ItemUtils.isAirOrNull(stack)) {
            File file = new File(core.getDataFolder(), "query_debug.json");
            if (file.exists()) {
                NBTQuery.of(file).ifPresent(nbtQuery -> {
                    NBTItem nbtItem = new NBTItem(stack);
                    NBTCompound result = nbtQuery.run(nbtItem);

                    /*System.out.println(result.toString());*/
                    if (args.length > 0) {
                        if (args[0].equalsIgnoreCase("true")) {
                            ItemStack stackToMergeIn = player.getEquipment().getItem(EquipmentSlot.OFF_HAND);
                            NBTItem nbtItem1 = new NBTItem(stackToMergeIn);
                            nbtItem1.mergeCompound(result);
                            nbtItem1.applyNBT(stackToMergeIn);
                        }
                    }
                });
            }
        }
        return true;
    }

}
