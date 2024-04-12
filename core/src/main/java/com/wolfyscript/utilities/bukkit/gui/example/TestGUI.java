package com.wolfyscript.utilities.bukkit.gui.example;

import com.wolfyscript.utilities.WolfyCore;
import com.wolfyscript.utilities.gui.GuiAPIManager;
import com.wolfyscript.utilities.gui.example.CounterExampleKotlinKt;

public class TestGUI {

    private final WolfyCore core;

    public TestGUI(WolfyCore core) {
        this.core = core;
    }

    public void initWithConfig() {
        GuiAPIManager manager = core.getWolfyUtils().getGuiManager();
        CounterExampleKotlinKt.registerExampleCounter(manager);
        KtStackEditorExampleKt.register(manager);
    }


}
