package com.wolfyscript.utilities.bukkit.nms.fallback;

import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.nms.BlockUtil;
import me.wolfyscript.utilities.api.nms.InventoryUtil;
import me.wolfyscript.utilities.api.nms.ItemUtil;
import me.wolfyscript.utilities.api.nms.NBTUtil;
import me.wolfyscript.utilities.api.nms.NMSUtil;
import me.wolfyscript.utilities.api.nms.NetworkUtil;
import me.wolfyscript.utilities.api.nms.RecipeUtil;
import me.wolfyscript.utilities.util.version.ServerVersion;
import org.apache.commons.lang3.NotImplementedException;

/**
 * Used when no NMSUtil is available for the server Minecraft version.
 */
public class FallbackNMSEntry extends NMSUtil {

    /**
     * The class that implements this NMSUtil needs to have a constructor with just the WolfyUtilities parameter.
     *
     * @param wolfyUtilities
     */
    public FallbackNMSEntry(WolfyUtilities wolfyUtilities) {
        super(wolfyUtilities);
        this.inventoryUtil = new FallbackInventoryUtilImpl(this);
    }

    @Override
    public BlockUtil getBlockUtil() {
        throw new NotImplementedException("BlockUtil is not yet implement for " + ServerVersion.getVersion());
    }

    @Override
    public ItemUtil getItemUtil() {
        throw new NotImplementedException("ItemUtil is not yet implement for " + ServerVersion.getVersion());
    }

    @Override
    public InventoryUtil getInventoryUtil() {
        throw new NotImplementedException("InventoryUtil is not yet implement for " + ServerVersion.getVersion());
    }

    @Override
    public NBTUtil getNBTUtil() {
        throw new NotImplementedException("NBTUtil is not yet implement for " + ServerVersion.getVersion());
    }

    @Override
    public RecipeUtil getRecipeUtil() {
        throw new NotImplementedException("RecipeUtil is not yet implement for " + ServerVersion.getVersion());
    }

    @Override
    public NetworkUtil getNetworkUtil() {
        throw new NotImplementedException("NetworkUtil is not yet implement for " + ServerVersion.getVersion());
    }
}
