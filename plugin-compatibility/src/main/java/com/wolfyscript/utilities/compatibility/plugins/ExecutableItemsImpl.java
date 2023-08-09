package com.wolfyscript.utilities.compatibility.plugins;

import com.google.inject.Inject;
import com.ssomar.score.api.executableitems.ExecutableItemsAPI;
import com.wolfyscript.utilities.bukkit.WolfyCoreBukkit;
import com.wolfyscript.utilities.bukkit.annotations.WUPluginIntegration;
import com.wolfyscript.utilities.bukkit.compatibility.plugins.ExecutableItemsIntegration;
import com.wolfyscript.utilities.bukkit.world.items.references.APIReference;
import com.wolfyscript.utilities.bukkit.compatibility.PluginIntegrationAbstract;
import com.wolfyscript.utilities.compatibility.plugins.executableitems.ExecutableItemsRef;
import org.bukkit.plugin.Plugin;

import java.util.List;

@WUPluginIntegration(pluginName = ExecutableItemsIntegration.PLUGIN_NAME)
public class ExecutableItemsImpl extends PluginIntegrationAbstract implements ExecutableItemsIntegration {

    /**
     * The main constructor that is called whenever the integration is created.<br>
     *
     * @param core       The WolfyUtilCore.
     */
    @Inject
    protected ExecutableItemsImpl(WolfyCoreBukkit core) {
        super(core, ExecutableItemsIntegration.PLUGIN_NAME);
    }

    @Override
    public boolean isAPIReferenceIncluded(APIReference reference) {
        return reference instanceof ExecutableItemsRef;
    }

    @Override
    public void init(Plugin plugin) {
        core.registerAPIReference(new ExecutableItemsRef.Parser(ExecutableItemsAPI.getExecutableItemsManager()));
    }

    @Override
    public boolean hasAsyncLoading() {
        return false;
    }

    @Override
    public boolean isValidID(String id) {
        return ExecutableItemsAPI.getExecutableItemsManager().isValidID(id);
    }

    @Override
    public List<String> getExecutableItemIdsList() {
        return ExecutableItemsAPI.getExecutableItemsManager().getExecutableItemIdsList();
    }
}
