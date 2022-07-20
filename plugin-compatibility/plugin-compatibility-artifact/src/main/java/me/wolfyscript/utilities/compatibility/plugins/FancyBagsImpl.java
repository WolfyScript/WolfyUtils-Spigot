package me.wolfyscript.utilities.compatibility.plugins;

import me.wolfyscript.utilities.annotations.WUPluginIntegration;
import me.wolfyscript.utilities.api.WolfyUtilCore;
import me.wolfyscript.utilities.compatibility.PluginIntegrationAbstract;
import me.wolfyscript.utilities.compatibility.plugins.fancybags.FancyBagsItemsRef;
import org.bukkit.plugin.Plugin;

@WUPluginIntegration(pluginName = FancyBagsImpl.KEY)
public class FancyBagsImpl extends PluginIntegrationAbstract {

    public static final String KEY = "FancyBags";

    /**
     * The main constructor that is called whenever the integration is created.<br>
     *
     * @param core       The WolfyUtilCore.
     */
    protected FancyBagsImpl(WolfyUtilCore core) {
        super(core, KEY);
    }

    @Override
    public void init(Plugin plugin) {
        core.registerAPIReference(new FancyBagsItemsRef.Parser());
    }

    @Override
    public boolean hasAsyncLoading() {
        return false;
    }
}
