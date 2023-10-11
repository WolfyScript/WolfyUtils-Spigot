package com.wolfyscript.utilities.bukkit.nms.item.crafting;

import me.wolfyscript.utilities.api.WolfyUtilCore;
import me.wolfyscript.utilities.util.version.MinecraftVersion;
import me.wolfyscript.utilities.util.version.ServerVersion;

public interface FunctionalRecipeGenerator {

    void generateRecipeClasses();

    Class<?> getFunctionalRecipeClass(FunctionalRecipeType type);

    boolean addRecipeToRecipeManager(FunctionalRecipe<?> recipe);

    static FunctionalRecipeGenerator create(WolfyUtilCore core) {
        return ServerVersion.isAfterOrEq(MinecraftVersion.of(1, 20, 0)) ? new EmptyFunctionalRecipeGenerator() : new DeprecatedFunctionalRecipeGenerator(core);
    }

    /**
     * Empty non-functional implementation of the FunctionalRecipeGenerator.
     * This is used on minecraft version after 1.19, due to availability of API alternatives in later versions.
     */
    class EmptyFunctionalRecipeGenerator implements FunctionalRecipeGenerator {

        @Override
        public void generateRecipeClasses() { }

        @Override
        public Class<?> getFunctionalRecipeClass(FunctionalRecipeType type) { return null; }

        @Override
        public boolean addRecipeToRecipeManager(FunctionalRecipe<?> recipe) { return false; }
    }

}
