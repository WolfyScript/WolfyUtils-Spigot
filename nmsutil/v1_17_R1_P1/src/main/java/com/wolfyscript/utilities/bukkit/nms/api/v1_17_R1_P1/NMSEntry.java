package com.wolfyscript.utilities.bukkit.nms.api.v1_17_R1_P1;

import com.wolfyscript.utilities.bukkit.WolfyUtilsBukkit;
import com.wolfyscript.utilities.bukkit.nms.api.NMSUtil;
import org.bukkit.plugin.Plugin;

public class NMSEntry extends NMSUtil {

    /**
     * The class that implements this NMSUtil needs to have a constructor with just the {@link Plugin} parameter.
     *
     * @param wolfyUtilities
     */
    public NMSEntry(WolfyUtilsBukkit wolfyUtilities) {
        super(wolfyUtilities);
        this.blockUtil = new BlockUtilImpl(this);
        this.itemUtil = new ItemUtilImpl(this);
        this.inventoryUtil = new InventoryUtilImpl(this);
        this.nbtUtil = new NBTUtilImpl(this);
        this.recipeUtil = new RecipeUtilImpl(this);
        this.networkUtil = new NetworkUtilImpl(this);
    }

}
