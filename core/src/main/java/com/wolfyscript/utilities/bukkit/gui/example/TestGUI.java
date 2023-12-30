package com.wolfyscript.utilities.bukkit.gui.example;

import com.wolfyscript.utilities.WolfyCore;
import com.wolfyscript.utilities.eval.value_provider.ValueProviderStringConst;
import com.wolfyscript.utilities.gui.GuiAPIManager;
import com.wolfyscript.utilities.gui.InteractionResult;
import com.wolfyscript.utilities.gui.Position;
import com.wolfyscript.utilities.gui.components.ButtonBuilder;
import com.wolfyscript.utilities.gui.components.ComponentClusterBuilder;
import com.wolfyscript.utilities.gui.example.CounterExample;
import com.wolfyscript.utilities.gui.signal.Signal;
import com.wolfyscript.utilities.world.items.ItemStackConfig;

public class TestGUI {

    private final WolfyCore core;

    public TestGUI(WolfyCore core) {
        this.core = core;
    }

    public void initWithConfig() {
        GuiAPIManager manager = core.getWolfyUtils().getGUIManager();
        CounterExample.register(manager);
        StackEditorExample.register(manager);
    }


}
