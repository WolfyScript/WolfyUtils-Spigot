package me.wolfyscript.utilities.compatibility.plugins;

import com.wolfyscript.utilities.bukkit.annotations.WUPluginIntegration;
import com.wolfyscript.utilities.bukkit.WolfyUtilCore;
import com.wolfyscript.utilities.bukkit.compatibility.PluginIntegrationAbstract;
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
