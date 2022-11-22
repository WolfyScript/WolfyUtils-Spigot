package me.wolfyscript.utilities.compatibility.plugins;

import com.wolfyscript.utilities.bukkit.annotations.WUPluginIntegration;
import me.wolfyscript.utilities.api.WolfyUtilCore;
import me.wolfyscript.utilities.api.inventory.custom_items.references.APIReference;
import com.wolfyscript.utilities.bukkit.compatibility.PluginIntegrationAbstract;
import me.wolfyscript.utilities.compatibility.plugins.denizen.DenizenRefImpl;
import me.wolfyscript.utilities.compatibility.plugins.eco.EcoRefImpl;
import org.bukkit.plugin.Plugin;

@WUPluginIntegration(pluginName = DenizenIntegrationImpl.PLUGIN_NAME)
public class DenizenIntegrationImpl extends PluginIntegrationAbstract {

    public static final String PLUGIN_NAME = "Denizen";

    /**
     * The main constructor that is called whenever the integration is created.<br>
     *
     * @param core       The WolfyUtilCore.
     */
    protected DenizenIntegrationImpl(WolfyUtilCore core) {
        super(core, PLUGIN_NAME);
    }

    @Override
    public boolean isAPIReferenceIncluded(APIReference reference) {
        return reference instanceof DenizenRefImpl;
    }

    @Override
    public void init(Plugin plugin) {
        core.registerAPIReference(new EcoRefImpl.Parser());
    }

    @Override
    public boolean hasAsyncLoading() {
        return false;
    }

}
