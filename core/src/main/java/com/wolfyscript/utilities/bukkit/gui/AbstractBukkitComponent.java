package com.wolfyscript.utilities.bukkit.gui;

import com.google.common.base.Preconditions;
import com.wolfyscript.utilities.KeyedStaticId;
import com.wolfyscript.utilities.NamespacedKey;
import com.wolfyscript.utilities.common.WolfyUtils;
import com.wolfyscript.utilities.common.gui.Component;

/**
 * <p>
 * Contains the common properties of all Components.
 * It makes it easier to create custom components.
 * </p>
 * <p>
 * Additional functionality should be implemented on a per-component basis without further inheritance, to make it easier to expand/change in the future.
 * Instead, use interfaces (that are already there for the platform independent API) and implement them for each component.
 * Duplicate code may occur, but it can be put into static methods.
 * </p>
 */
public abstract class AbstractBukkitComponent implements Component {

    private final NamespacedKey type;
    private final String internalID;
    private final WolfyUtils wolfyUtils;
    private final Component parent;

    public AbstractBukkitComponent(String internalID, WolfyUtils wolfyUtils, Component parent) {
        Preconditions.checkNotNull(internalID);
        Preconditions.checkNotNull(wolfyUtils);
        this.type = wolfyUtils.getIdentifiers().getNamespaced(getClass());
        Preconditions.checkNotNull(type, "Missing type key! One must be provided to the Component using the annotation: %s", KeyedStaticId.class.getName());
        this.internalID = internalID;
        this.wolfyUtils = wolfyUtils;
        this.parent = parent;
    }

    @Override
    public NamespacedKey getNamespacedKey() {
        return type;
    }

    @Override
    public String getID() {
        return internalID;
    }

    @Override
    public WolfyUtils getWolfyUtils() {
        return wolfyUtils;
    }

    @Override
    public Component parent() {
        return parent;
    }

}
