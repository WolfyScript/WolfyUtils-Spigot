package com.wolfyscript.utilities.bukkit.gui;

import com.wolfyscript.utilities.bukkit.world.items.BukkitItemStackConfig;
import com.wolfyscript.utilities.common.WolfyUtils;
import com.wolfyscript.utilities.common.gui.Button;
import com.wolfyscript.utilities.common.gui.ButtonBuilder;
import com.wolfyscript.utilities.common.gui.Component;
import com.wolfyscript.utilities.common.gui.ComponentState;
import com.wolfyscript.utilities.common.gui.GuiHolder;
import com.wolfyscript.utilities.common.gui.InteractionCallback;
import com.wolfyscript.utilities.common.gui.InteractionDetails;
import com.wolfyscript.utilities.common.gui.InteractionResult;
import com.wolfyscript.utilities.common.gui.SizedComponent;
import com.wolfyscript.utilities.common.items.ItemStackConfig;
import org.bukkit.inventory.ItemStack;

public class ButtonImpl implements Button {

    private final WolfyUtils wolfyUtils;
    private final String id;
    private final SizedComponent parent;
    private final InteractionCallback interactionCallback;
    private final ItemStackConfig<?> icon;

    public ButtonImpl(WolfyUtils wolfyUtils, String id, SizedComponent parent, ItemStackConfig<?> icon, InteractionCallback interactionCallback) {
        this.wolfyUtils = wolfyUtils;
        this.id = id;
        this.parent = parent;
        this.icon = icon;
        this.interactionCallback = interactionCallback;
    }

    public ItemStackConfig<?> getIcon() {
        return icon;
    }

    @Override
    public String getID() {
        return id;
    }

    @Override
    public WolfyUtils getWolfyUtils() {
        return wolfyUtils;
    }

    @Override
    public SizedComponent parent() {
        return parent;
    }

    @Override
    public void init() {

    }

    @Override
    public Class<? extends ComponentState> getComponentStateType() {
        return ButtonStateImpl.class;
    }

    @Override
    public InteractionResult interact(GuiHolder guiHolder, ComponentState componentState, InteractionDetails interactionDetails) {
        return interactionCallback.interact(guiHolder, componentState, interactionDetails);
    }

    @Override
    public InteractionCallback interactCallback() {
        return interactionCallback;
    }

    public static class Builder implements ButtonBuilder, ComponentBuilder<Button, SizedComponent> {

        private final String id;
        private final WolfyUtils wolfyUtils;
        private BukkitItemStackConfig icon;
        private InteractionCallback interactionCallback;

        public Builder(String id, WolfyUtils wolfyUtils) {
            this.wolfyUtils = wolfyUtils;
            this.id = id;
        }

        @Override
        public ButtonBuilder icon(ItemStackConfig<?> itemStackConfig) {
            if (itemStackConfig instanceof BukkitItemStackConfig bukkitItemStackConfig) {
                this.icon = bukkitItemStackConfig;
            }
            return this;
        }

        @Override
        public ButtonBuilder icon(String s) {
            this.icon = new BukkitItemStackConfig(wolfyUtils, s);
            return this;
        }

        @Override
        public ButtonBuilder interact(InteractionCallback interactionCallback) {
            this.interactionCallback = interactionCallback;
            return this;
        }

        @Override
        public Button create(SizedComponent parent) {
            return new ButtonImpl(wolfyUtils, id, parent, icon, interactionCallback);
        }
    }

}
