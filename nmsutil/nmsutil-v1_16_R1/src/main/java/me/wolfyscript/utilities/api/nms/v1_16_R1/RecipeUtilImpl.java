package me.wolfyscript.utilities.api.nms.v1_16_R1;

import com.wolfyscript.utilities.bukkit.nms.api.RecipeUtil;
import com.wolfyscript.utilities.bukkit.nms.api.NMSUtil;
import com.wolfyscript.utilities.bukkit.nms.api.inventory.RecipeType;
import me.wolfyscript.utilities.api.nms.v1_16_R1.inventory.RecipeIterator;
import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

public class RecipeUtilImpl extends RecipeUtil {

    protected RecipeUtilImpl(NMSUtil nmsUtil) {
        super(nmsUtil);
    }

    @Override
    public @NotNull Iterator<Recipe> recipeIterator(RecipeType recipeType) {
        return new RecipeIterator(recipeType);
    }

}
