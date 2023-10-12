package me.wolfyscript.utilities.api.nms.v1_18_R2;

import me.wolfyscript.utilities.util.NamespacedKey;
import net.minecraft.resources.ResourceLocation;

public class NamespacedKeyUtils {

    private NamespacedKeyUtils(){}

    public static ResourceLocation toMC(NamespacedKey key) {
        return new ResourceLocation(key.getNamespace(), key.getKey());
    }
}
