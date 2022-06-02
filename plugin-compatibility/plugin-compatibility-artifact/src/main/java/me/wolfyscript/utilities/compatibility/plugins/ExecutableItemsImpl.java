package me.wolfyscript.utilities.compatibility.plugins;

import com.ssomar.score.api.executableitems.ExecutableItemsAPI;
import me.wolfyscript.utilities.annotations.WUPluginIntegration;
import me.wolfyscript.utilities.api.WolfyUtilCore;
import me.wolfyscript.utilities.api.inventory.custom_items.references.APIReference;
import me.wolfyscript.utilities.compatibility.PluginIntegrationAbstract;
import me.wolfyscript.utilities.compatibility.plugins.executableitems.ExecutableItemsRef;
import org.bukkit.plugin.Plugin;

import java.util.List;

@WUPluginIntegration(pluginName = ExecutableItemsIntegration.PLUGIN_NAME)
public class ExecutableItemsImpl extends PluginIntegrationAbstract implements ExecutableItemsIntegration {

    /**
     * The main constructor that is called whenever the integration is created.<br>
     *
     * @param core       The WolfyUtilCore.
     */
    protected ExecutableItemsImpl(WolfyUtilCore core) {
        super(core, ExecutableItemsIntegration.PLUGIN_NAME);
    }

    @Override
    public boolean isAPIReferenceIncluded(APIReference reference) {
        return super.isAPIReferenceIncluded(reference);
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
