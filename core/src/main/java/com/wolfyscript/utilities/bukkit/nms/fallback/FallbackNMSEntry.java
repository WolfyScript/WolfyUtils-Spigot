package com.wolfyscript.utilities.bukkit.nms.fallback;

import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.nms.ItemUtil;
import me.wolfyscript.utilities.api.nms.NBTUtil;
import me.wolfyscript.utilities.api.nms.NMSUtil;
import me.wolfyscript.utilities.api.nms.NetworkUtil;

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
        this.blockUtil = new FallbackBlockUtilImpl(this);
        this.itemUtil = new ItemUtil(this) { };
        this.nbtUtil = new NBTUtil(this) { };
        this.recipeUtil = new FallbackRecipeUtilImpl(this);
        this.networkUtil = new NetworkUtil(this) { };
    }

}
