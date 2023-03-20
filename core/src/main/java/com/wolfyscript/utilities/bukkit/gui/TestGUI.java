package com.wolfyscript.utilities.bukkit.gui;

import com.wolfyscript.utilities.bukkit.WolfyCoreBukkit;
import com.wolfyscript.utilities.common.gui.GuiAPIManager;
import com.wolfyscript.utilities.common.gui.InteractionResult;
import com.wolfyscript.utilities.common.gui.WindowType;
import net.kyori.adventure.text.Component;

public class TestGUI {

    private WolfyCoreBukkit core;

    public TestGUI(WolfyCoreBukkit core) {
        this.core = core;
    }

    public void init() {
        GuiAPIManager manager = core.getWolfyUtils().getGUIManager();

        manager.registerRouter("main", builder -> builder
                .children(children -> children
                        .window("mainmenu", mainMenu -> mainMenu
                                .title((guiHolder, window) -> Component.text("Main Menu"))
                                .size(27)
                                .render((guiHolder, state) -> {
                                   // Place child components into the GUI
                                    state.setComponent(13, "settings"); // references the registered child component with the id "settings"
                                })
                                .children(mainMenuChildren -> mainMenuChildren
                                        .button("settings", settingsBtn -> settingsBtn
                                                .icon("minecraft:redstone_dust")
                                                .interact((guiHolder, componentState, interactionDetails) -> {
                                                    // Handle interaction
                                                    return InteractionResult.cancel(true);
                                                })
                                        )
                                )
                        )
                        .window("settings", settings -> settings
                                .title((guiHolder, window) -> Component.text("Settings"))
                                .type(WindowType.HOPPER)
                                .render((guiHolder, WindowState) -> {
                                    // Render components into the GUI
                                })
                        )
                )
        );
    }


}
