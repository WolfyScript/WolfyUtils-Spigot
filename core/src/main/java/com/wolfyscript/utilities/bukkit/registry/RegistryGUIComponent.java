package com.wolfyscript.utilities.bukkit.registry;

import com.wolfyscript.utilities.NamespacedKey;
import com.wolfyscript.utilities.common.gui.Component;
import com.wolfyscript.utilities.common.registry.Registries;
import com.wolfyscript.utilities.common.registry.UniqueTypeRegistrySimple;

public class RegistryGUIComponent extends UniqueTypeRegistrySimple<Component> {

    public RegistryGUIComponent(NamespacedKey key, Registries registries) {
        super(key, registries);
    }

}
